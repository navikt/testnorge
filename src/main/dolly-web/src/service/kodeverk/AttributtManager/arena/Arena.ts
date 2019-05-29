import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Arena,
		subKategori: SubKategorier.Arena,
		id: 'arenaforvalter',
		label: 'Er arbeidssøker',
		dataSource: DataSource.ARENA,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Arena,
				subKategori: SubKategorier.Arena,
				id: 'arenaBrukertype',
				label: 'Brukertype',
				dataSource: DataSource.ARENA,
				inputType: InputType.Select,
				options: SelectOptionsManager('arenaBrukertype'),
				validation: yup.string().required('Velg en brukertype'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Arena,
				subKategori: SubKategorier.Arena,
				id: 'kvalifiseringsgruppe',
				label: 'Servicebehov',
				dataSource: DataSource.ARENA,
				inputType: InputType.Select,
				size: 'large',
				validation: yup.string().when('arenaBrukertype', {
					is: 'MED_SERVICEBEHOV',
					then: yup.string().required('Velg et servicebehov')
				}),
				options: SelectOptionsManager('kvalifiseringsgruppe'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
