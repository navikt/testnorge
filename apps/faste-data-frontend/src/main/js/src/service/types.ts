type Person = {
  ident: string;
  fornavn?: string;
  mellomnavn?: string;
  etternavn?: string;
  foedselsdato?: string;
  adresse?: Adresse;
  opprinnelse?: string;
  tags: string[];
};

type Adresse = {
  gatenavn?: string;
  postnummer?: string;
  poststed?: string;
  kommunenummer?: string;
};
