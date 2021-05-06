import {
  Adresse as FasteDataAdresse,
  Organisasjon as FasteDataOrganisasjon,
} from '@/service/OrganisasjonFasteDataService';
import { Adresse, Organisasjon } from '@/service/OrganisasjonService';

type Field = {
  key: string;
  value: unknown;
};

type CompareField = {
  left: Field;
  right: Field;
};

type CompareItem = {
  item: any;
  key: string;
};

const compareFields = (left: CompareItem, right: CompareItem) => ({
  equal:
    (!left.item[left.key] && !right.item[right.key]) ||
    (!!left.item[left.key] && left.item[left.key].toUpperCase() === right.item[right.key]),
  comparedField: {
    left: {
      key: left.key,
      value: left.item[left.key],
    },
    right: {
      key: right.key,
      value: right.item[right.key],
    },
  },
});

const compare = (left: FasteDataOrganisasjon, right: Organisasjon) => {
  const mismatchFields: CompareField[] = [];

  function checkMismatch<T, I>(leftKey: keyof T, rightKey: keyof I) {
    const { equal, comparedField } = compareFields(
      { item: left, key: leftKey.toString() },
      { item: right, key: rightKey.toString() }
    );
    if (!equal) {
      mismatchFields.push(comparedField);
    }
  }

  checkMismatch<FasteDataOrganisasjon, Organisasjon>('orgnummer', 'orgnummer');
  checkMismatch<FasteDataOrganisasjon, Organisasjon>('enhetstype', 'enhetType');
  checkMismatch<FasteDataOrganisasjon, Organisasjon>('overenhet', 'juridiskEnhet');
  checkMismatch<FasteDataOrganisasjon, Organisasjon>('navn', 'navn');
  checkMismatch<FasteDataOrganisasjon, Organisasjon>('redigertNavn', 'redigertnavn');

  checkMismatch<FasteDataAdresse, Adresse>('adresselinje1', 'adresselinje1');
  checkMismatch<FasteDataAdresse, Adresse>('adresselinje2', 'adresselinje2');
  checkMismatch<FasteDataAdresse, Adresse>('adresselinje3', 'adresselinje3');
  checkMismatch<FasteDataAdresse, Adresse>('postnr', 'postnr');
  checkMismatch<FasteDataAdresse, Adresse>('kommunenr', 'kommunenr');
  checkMismatch<FasteDataAdresse, Adresse>('kommunenr', 'kommunenr');

  return {
    mismatchFields,
    isMismatch: mismatchFields.length > 0,
  };
};

export default {
  compare,
};
