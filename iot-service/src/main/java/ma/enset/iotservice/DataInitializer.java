package ma.enset.iotservice;


import lombok.extern.slf4j.Slf4j;
import ma.enset.iotservice.entities.IotSensor;
import ma.enset.iotservice.entities.WaterRessource;
import ma.enset.iotservice.enums.RessourceType;
import ma.enset.iotservice.enums.SensorType;
import ma.enset.iotservice.model.Location;
import ma.enset.iotservice.repository.IotSensorRepository;
import ma.enset.iotservice.repository.SensorReadingRepository;
import ma.enset.iotservice.repository.WaterRessourceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Component
@Slf4j

public class DataInitializer implements CommandLineRunner {
    @Autowired
    private WaterRessourceRepository waterRessourceRepository;
    @Autowired
    private IotSensorRepository iotSensorRepository;
    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (sensorReadingRepository.count() > 0) return; //pour verifier si les capteurs existent deja
        log.info("=======  Initialisation des donnes ======== ");
        InputStream stream = new ClassPathResource("data/maroc_ressources_eau.xlsx").getInputStream();
        Workbook workbook = new XSSFWorkbook(stream);
        loadBarrages(workbook);
        loadLacs(workbook);
        loadRivieres(workbook);
        loadNappe(workbook);
        loadSensor();
        workbook.close();
        stream.close();
        log.info("{} capteurs et {} ressources eau initialisés",
                iotSensorRepository.count(), waterRessourceRepository.count());
    }

    private static final Map<String, Long> regionToCity = new LinkedHashMap<>() {
        {
            put("El Kelaa des Sraghna", 24L);
            put("Drâa-Tafilalet", 23L); // ouarzazate
            // put("Drâa", 24L);
            put("Ouarzazate", 23L);
            put("Errachidia", 20L);
            put("Er-Rachidia", 20L);
            put("Zagora", 25L);
            put("erfoud", 40L);
            put("Tinghir", 41L);

            // Marrakech-Safi (APRÈS Drâa)
            put("Marrakech", 4L);
            put("Safi", 16L);
            put("Asfi", 16L);

            // Grand Casablanca-Settat
            put("Grand Casablanca", 1L);
            put("Casablanca", 1L);
            put("Settat", 13L);
            put("Chaouia", 13L);
            put("benslimane", 37L);
            put("El Jadida", 39L);

            // Rabat-Salé-Kénitra
            put("Rabat", 2L);
            put("Kenitra", 17L);
            put("Khemisset", 35L);
            put("sale", 36L);


            // Fès-Meknès
            put("Fès", 3L);
            put("Fez", 3L);
            put("Meknès", 7L);
            put("Meknes", 7L);
            put("Taza", 11L);
            put("Taounate", 19L);
            put("ifrane", 38L);
            put("Ksar el-Kebir",42L);
            put("Ouazzane",43L);

            // Souss-Massa
            put("Souss-Massa", 5L); // Agadir
            put("Agadir", 5L);
            put("Taroudant", 26L);
            put("Tiznit", 27L);

            // Tanger-Tétouan
            put("Tanger", 6L);
            put("Tétouan", 28L);
            put("Al Hoceima", 9L);
            put("Larache", 29L);

            // Oriental
            put("Oriental", 8L); // Oujda
            put("Oujda", 8L);
            put("Nador", 30L);
            put("Berkane", 18L);
            put("Taourirt", 32L);
            put("Guercif", 34L);

            // Béni Mellal-Khénifra
            put("Béni Mellal", 15L);
            put("Beni Mellal", 15L);
            put("Khenifra", 31L);
            put("Azilal", 12L);
            put("Oued Zem", 10L);
            put("Khouribga", 14L);

            // Laâyoune-Dakhla
            put("Laayoune", 22L);
            put("Laâyoune", 22L);
            put("Dakhla", 21L);
            put("Guelmim", 33L);
        }
    };

    private Long resolveCityLocationId(String region) {
        if (region == null || region.isBlank()) return null;

        // La colonne est au format "Ville / Région" (ex: "Benslimane / Casablanca-Settat").
        // On isole la partie AVANT le "/" — c'est la ville précise, toujours plus fiable
        // que la région générale qui contient souvent le nom d'une grande ville (ex: "Casablanca-Settat").
        String specificPart = region.contains("/")
                ? region.split("/")[0].trim()
                : region.trim();

        // 1) Tentative de correspondance sur la partie précise (ville/province)
        Long match = findMatch(specificPart);
        if (match != null) return match;

        // 2) Repli : recherche dans la chaîne complète (région), seulement si rien trouvé avant
        match = findMatch(region);
        if (match != null) return match;

        log.warn("Aucune ville trouvée pour la région: {}", region);
        return null;
    }

    private Long findMatch(String text) {
        String lower = text.toLowerCase();
        for (Map.Entry<String, Long> entry : regionToCity.entrySet()) {
            if (lower.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }


    //Barrages
    private void loadBarrages(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Barrages");
        int load = 0;
        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue;
            Cell cellId = row.getCell(0);
            if (cellId == null | cellId.getCellType() == CellType.BLANK) continue;
            try {
                long id = (long) cellId.getNumericCellValue();
                String name = getString(row, 1);
                String region = getString(row, 3);
                Long cityLocId = resolveCityLocationId(region);
                String coord = getString(row, 4);
                double capaciteMax = getDouble(row, 5);
                double currentLevel = getDouble(row, 6);
                String[] latLon = coord.split(",");
                double lat = Double.parseDouble(latLon[0].trim());
                double lon = Double.parseDouble(latLon[1].trim());

                waterRessourceRepository.save(WaterRessource.builder()
                        .name(name)
                        .capaciteMax(capaciteMax)
                        .currentLevel(currentLevel)
                        .locationId(id)
                        .cityLocationId(cityLocId)
                        .location(Location.builder()
                                .id(id)
                                .nameCity(region)
                                .latitude(lat)
                                .longitude(lon)
                                .build())
                        .ressourceType(RessourceType.BARRAGE)
                        .lastUpdate(LocalDateTime.now())
                        .build());
                load++;
            } catch (Exception e) {
                log.warn("Erreur ligne barrage {}: {}", row.getRowNum(), e.getMessage());
            }
        }
        log.info("{} barrages chargés", load);
    }

    //Lacs
    private void loadLacs(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Lacs");
        int load = 0;
        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue;
            Cell cellId = row.getCell(0);
            if (cellId == null | cellId.getCellType() == CellType.BLANK) continue;
            try {
                long id = (long) cellId.getNumericCellValue() + 100;
                String name = getString(row, 1);
                String region = getString(row, 3);
                Long cityLocId = resolveCityLocationId(region);
                String coord = getString(row, 4);
                double currentLevel = getDouble(row, 7);
                String[] latLon = coord.split(",");
                double lat = Double.parseDouble(latLon[0].trim());
                double lon = Double.parseDouble(latLon[1].trim());
                waterRessourceRepository.save(WaterRessource.builder()
                        .name(name)
                        .locationId(id)
                        .cityLocationId(cityLocId)
                        .location(Location.builder()
                                .id(id)
                                .nameCity(region)
                                .latitude(lat)
                                .longitude(lon)
                                .build())
                        .ressourceType(RessourceType.LAC)
                        .capaciteMax(100)
                        .currentLevel(currentLevel)
                        .lastUpdate(LocalDateTime.now())
                        .build());
                load++;
            } catch (Exception e) {
                log.warn("Erreur ligne lac {}: {}", row.getRowNum(), e.getMessage());
            }
        }
        log.info("{} lacs chargés", load);
    }

    //Rivieres
    public void loadRivieres(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Rivières");
        int load = 0;
        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue;
            Cell cellId = row.getCell(0);
            if (cellId == null | cellId.getCellType() == CellType.BLANK) continue;
            try {
                long id = (long) cellId.getNumericCellValue() + 200;
                String name = getString(row, 1);
                String region = getString(row, 3);
                Long cityLocId = resolveCityLocationId(region);
                double debitMoyen = getDouble(row, 5);
                double debitActuel = getDouble(row, 6);
                waterRessourceRepository.save(WaterRessource.builder()
                        .name(name)
                        .locationId(id)
                        .cityLocationId(cityLocId)
                        .location(Location.builder()
                                .id(id)
                                .nameCity(region)
                                .build())
                        .ressourceType(RessourceType.RIVIERE)
                        .capaciteMax(debitMoyen)
                        .currentLevel(debitActuel)
                        .lastUpdate(LocalDateTime.now())
                        .build());
                load++;
            } catch (Exception e) {
                log.warn("Erreur ligne riviere {}: {}", row.getRowNum(), e.getMessage());
            }
        }
        log.info("{} rivieres chargés", load);
    }

    //Nappes phréatiques
    public void loadNappe(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Nappes phréatiques");
        int load = 0;
        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue;
            Cell cellId = row.getCell(0);
            if (cellId == null | cellId.getCellType() == CellType.BLANK) continue;
            try {
                long id = (long) cellId.getNumericCellValue() + 300;
                String name = getString(row, 1);
                String region = getString(row, 3);
                Long cityLocId = resolveCityLocationId(region);
                double niveauActuel = getDouble(row, 6);
                double niveauNormal = getDouble(row, 7);
                waterRessourceRepository.save(WaterRessource.builder()
                        .name(name)
                        .locationId(id)
                        .cityLocationId(cityLocId)
                        .location(Location.builder()
                                .id(id)
                                .nameCity(region)
                                .build())
                        .ressourceType(RessourceType.NAPPE_PHREATIQUE)
                        .capaciteMax(niveauNormal)
                        .currentLevel(niveauActuel)
                        .lastUpdate(LocalDateTime.now())
                        .build());
                load++;
            } catch (Exception e) {
                log.warn("Erreur ligne Nappes phréatiques {}: {}", row.getRowNum(), e.getMessage());
            }
        }
        log.info("{} Nappes phréatiques chargés", load);
    }

    //Capteur Iot
    private void loadSensor() {
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur Air Casablanca Centre")
                .sensorType(SensorType.AIR_QUALITY)
                .locationId(1L)
                .location(Location.builder()
                        .id(1L)
                        .nameCity("Casablanca")
                        .build())
                .unite("IQA")
                .lastReadingDate(LocalDateTime.now())
                .description("Indice qualité air centre-ville")
                .build());
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur Poussières Casablanca")
                .sensorType(SensorType.POUSSIERE)
                .locationId(1L)
                .location(Location.builder()
                        .id(1L)
                        .nameCity("Casablanca")
                        .build())
                .unite("µg/m³")
                .lastReadingDate(LocalDateTime.now())
                .description("Particules PM2.5")
                .build());
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur Air Rabat")
                .sensorType(SensorType.AIR_QUALITY)
                .locationId(2L)
                .location(Location.builder()
                        .id(2L)
                        .nameCity("Rabat")
                        .build())
                .unite("IQA")
                .lastReadingDate(LocalDateTime.now())
                .description("Indice qualité air Rabat")
                .build());
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur CO2 Fès")
                .sensorType(SensorType.CO2)
                .locationId(3L)
                .location(Location.builder()
                        .id(3L)
                        .nameCity("Fes")
                        .build())
                .unite("ppm")
                .lastReadingDate(LocalDateTime.now())
                .description("Taux CO2 Fès")
                .build());
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur Température Marrakech")
                .sensorType(SensorType.TEMPERATURE)
                .locationId(4L)
                .location(Location.builder()
                        .id(4L)
                        .nameCity("Marrakech")
                        .build())
                .unite("°C")
                .lastReadingDate(LocalDateTime.now())
                .description("Température air Marrakech")
                .build());
        iotSensorRepository.save(IotSensor.builder()
                .name("Capteur Humidité Sol Agadir")
                .sensorType(SensorType.HUMIDITY_SOL)
                .locationId(5L)
                .location(Location.builder()
                        .id(5L)
                        .nameCity("Agadir")
                        .build())
                .unite("%")
                .lastReadingDate(LocalDateTime.now())
                .description("Humidité sol agricole Agadir")
                .build());
        log.info("{} capteurs chargés", iotSensorRepository.count());
    }

    private String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private double getDouble(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null | cell.getCellType() == CellType.BLANK) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try {
            return Double.parseDouble(cell.getStringCellValue().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

}


