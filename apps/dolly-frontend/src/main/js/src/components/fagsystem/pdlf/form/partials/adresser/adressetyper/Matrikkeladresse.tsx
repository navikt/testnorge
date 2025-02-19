import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'

interface MatrikkeladresseValues {
	path: string
}

export const Matrikkeladresse = ({ path }: MatrikkeladresseValues) => {
	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
			<FormTextInput name={`${path}.gaardsnummer`} label="Gårdsnummer" type="number" />
			<FormTextInput name={`${path}.bruksnummer`} label="Bruksnummer" type="number" />
			<FormTextInput name={`${path}.bruksenhetsnummer`} label="Bruksenhetsnummer" />
			<FormTextInput name={`${path}.tilleggsnavn`} label="Tilleggsnavn" />
			<FormSelect
				name={`${path}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
				size="large"
				isClearable={false}
			/>
			<FormSelect
				name={`${path}.kommunenummer`}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}
