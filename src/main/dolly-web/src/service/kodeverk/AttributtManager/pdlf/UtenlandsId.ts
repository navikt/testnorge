import { Kategorier, SubKategorier, SubSubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.UtenlandsId,
		id: 'utenlandskIdentifikasjonsnummer',
		label: 'Har utenlandsk ID',
		dataSource: DataSource.PDLF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.UtenlandsId,
				id: 'identifikasjonsnummer',
				label: 'Identifikasjonsnummer',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				validation: yup.string().required('Skriv et identifikasjonsnummer'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.UtenlandsId,
				id: 'opphoert',
				label: 'Opphørt',
				dataSource: DataSource.PDLF,
				inputType: InputType.Select,
				validation: yup.string().required('Velg en verdi'),
				options: SelectOptionsManager('boolean'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.UtenlandsId,
				id: 'utstederland',
				label: 'Utstederland',
				dataSource: DataSource.PDLF,
				inputType: InputType.Select,
				apiKodeverkId: 'StatsborgerskapFreg',
				validation: yup.string().required('Velg et land'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
