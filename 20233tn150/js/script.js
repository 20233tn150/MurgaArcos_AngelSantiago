var imgGears = ['img/Gears1.png', 'img/Gears2.png', 'img/Gears3.png', 'img/Gears4.png', 'img/Gears5.png'],
contador = 0;

function carruselGow(contenedor) {
    contenedor.addEventListener('click', e => {
        let retroceder = contenedor.querySelector('.retroceder'),
        avanzar = contenedor.querySelector('.avanzar'),
        img = contenedor.querySelector('img'),
        tgt = e.target;

        if (tgt == retroceder) {
            if(contador > 0){
                img.src = imgGears[contador - 1];
                contador--;
            } else{
                img.src = imgGears[imgGears.length - 1];
                contador = imgGears.length - 1;
            }
        } else if (tgt == avanzar) {
            if(contador < imgGears.length - 1){
                img.src = imgGears[contador + 1];
                contador++;
            } else{
                img.src = imgGears[0];
                contador = 0;
            }
        }
    });
} 

document.addEventListener("DOMContentLoaded", () => {
    let cont = document.querySelector('.contenedor');

    carruselGow(cont);
});