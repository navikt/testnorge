import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'

interface PostboksadresseValues {
	path: string
}

export const Postboksadresse = ({ path }: PostboksadresseValues) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormTextInput name={`${path}.postbokseier`} label="Postbokseier" size="large" />
			<FormTextInput name={`${path}.postboks`} label="Postboks" />
			<FormSelect
				name={`${path}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				size="large"
			/>
		</div>
	)
}
