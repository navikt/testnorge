import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'

interface MatrikkeladresseValues {
	path: string
}

export const Matrikkeladresse = ({ path }: MatrikkeladresseValues) => {
	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
			<FormikTextInput name={`${path}.gaardsnummer`} label="Gårdsnummer" type="number" />
			<FormikTextInput name={`${path}.bruksnummer`} label="Bruksnummer" type="number" />
			<FormikTextInput name={`${path}.bruksenhetsnummer`} label="Bruksenhetsnummer" />
			<FormikTextInput name={`${path}.tilleggsnavn`} label="Tilleggsnavn" />
			<FormikSelect
				name={`${path}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
				size="large"
				isClearable={false}
			/>
			<FormikSelect
				name={`${path}.kommunenummer`}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}
