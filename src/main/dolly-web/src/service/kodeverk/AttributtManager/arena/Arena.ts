import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

// import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Arena,
		subKategori: SubKategorier.Arena,
		id: 'arenaforvalter',
		label: 'Er arbeidssøker',
		dataSource: DataSource.ARENA,
		attributtType: AttributtType.SelectAndEdit,
		// validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Arena,
				subKategori: SubKategorier.Arena,
				id: 'arenaBrukertype',
				label: 'Brukertype',
				dataSource: DataSource.ARENA,
				inputType: InputType.Select,
				// validation: yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
				options: SelectOptionsManager('arenaBrukertype'),
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
				// validation: yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
				options: SelectOptionsManager('kvalifiseringsgruppe'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
