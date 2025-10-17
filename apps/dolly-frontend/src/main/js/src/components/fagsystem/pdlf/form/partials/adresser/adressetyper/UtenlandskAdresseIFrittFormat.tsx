import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { GtKodeverk } from '@/config/kodeverk'
import { FrittFormatAdresse } from './FrittFormatAdresse'

interface UtenlandskAdresseIFrittFormatValues {
	path: string
}

export const UtenlandskAdresseIFrittFormat = ({ path }: UtenlandskAdresseIFrittFormatValues) => {
	return (
		<FrittFormatAdresse
			path={path}
			extraFields={(p: string) => (
				<>
					<FormTextInput name={`${p}.postkode`} label="Postkode" />
					<FormTextInput name={`${p}.byEllerStedsnavn`} label="By eller sted" />
					<FormSelect
						name={`${p}.landkode`}
						label="Land"
						kodeverk={GtKodeverk.LAND}
						isClearable={false}
						size="large"
					/>
				</>
			)}
		/>
	)
}
