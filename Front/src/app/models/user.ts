export class User {
user!: {
    usuario: string;
    identificacion: string;
    tipoCliente: string;
    tipoPersona: string;
    nombres: string;
    usuarioInterno: string;
    ruta:string;
    roles:[
        nombre:string,
        ruta:string,
        menu: [
            {
                nombre: string;
                descripcion: string;
                url: string;
                orden: number;
                subMenu: [
                    {
                        nombre: string;
                        descripcion: string;
                        url: string;
                        orden: number;
                        subMenu: any;
                        operacion: string;
                    }
                ];
                operacion: string;
            }
        ]
    ],
    menu: [
        {
            nombre: string;
            descripcion: string;
            url: string;
            orden: number;
            subMenu: [
                {
                    nombre: string;
                    descripcion: string;
                    url: string;
                    orden: number;
                    subMenu: any;
                    operacion: string;
                }
            ];
            operacion: string;
        }
    ]
};
token!: string;
mensaje!:string;
}

