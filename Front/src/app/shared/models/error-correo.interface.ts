import { HttpErrorResponse } from "@angular/common/http"

export interface ErrorCorreoInterface extends HttpErrorResponse {
    error: {
       mensaje?: string
    }
 }