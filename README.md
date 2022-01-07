
#Projet NFC

Les Membres du groupe:
- Chaourar IMINE
- Idir LACHEB
- MERZOUK yugurten
- TAOUALIT Madjid 

<h3>Le fonctionnement de l'application:</h3>

Vous pouvez choisir l'examen à l'aide d'un spinner. Les horaires de
chaque examen sont fixés dans la méthode onCreate, concernant l'examen
d'Android heure de début = current time - 2 heure et heure fin = current time + 2 heures.


Si vous essayiez de scanner une carte dans un horaire qui ne correspond pas
à l'examen choisi, vous auriez un Toast indiquant vous avez mal choisi l'examen.

Une durée d'une minute doit être respectée entre le scan de rentré et de sortie.

Si l'étudiant n'existait pas dans la base de donnée(Les shared preferences) un petit
formulaire vous demandant le nom et le prénom apparaitrait, une fois les champs
sont renseignés, un click sur save permet d'ajouter l'étudiant dans les shared preferences,
il faut alors rescanner cette carte pour enregistrer la date de rentré.


Le pdf peut être généré si ou moins un étudiant est rentré, ils se trouvent dans le
dossier document de votre téléphone : ExamanXXX.pdf



