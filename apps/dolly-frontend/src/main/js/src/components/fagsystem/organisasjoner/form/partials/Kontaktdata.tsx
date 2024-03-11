import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { kontaktPaths } from '../paths'

type KontaktdataProps = {
	path: string
}

export const Kontaktdata = ({ path }: KontaktdataProps) => {
	return (
		<Kategori title="Kontaktdata" vis={kontaktPaths} flexRow={true}>
			<FormTextInput name={`${path}.telefon`} label="Telefon" size="large" type="number" />
			<FormTextInput name={`${path}.epost`} label="E-postadresse" size="large" />
			<FormTextInput name={`${path}.nettside`} label="Internettadresse" size="large" />
		</Kategori>
	)
}
