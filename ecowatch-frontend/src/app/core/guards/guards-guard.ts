import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthGuardData, createAuthGuard} from 'keycloak-angular';
import {inject} from '@angular/core';

const isAccessAllowed=async(
  routes:ActivatedRouteSnapshot,
  _:RouterStateSnapshot,
  authData:AuthGuardData
):Promise<boolean|UrlTree>=>{
  const{authenticated, grantedRoles}=authData;
  console.log('authenticated',authenticated);
  console.log('grantedRoles',grantedRoles);
  console.log('realmRoles',grantedRoles.realmRoles);
  //recuperer le role demande
  const requiredRole=routes.data['role'];
  if (!requiredRole){
    return false;
  }
  const hasRequiredRole=(role:string):boolean=>
    grantedRoles.realmRoles.includes(role);
  if (authenticated && hasRequiredRole(requiredRole)){
    return true;
  }
  const router=inject(Router);
  return false

}
export const guardsGuard: CanActivateFn = createAuthGuard<CanActivateFn>(isAccessAllowed);

