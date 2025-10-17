import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FrittFormatAdresse } from './FrittFormatAdresse'

interface PostadresseIFrittFormatValues {
	path: string
}

export const PostadresseIFrittFormat = ({ path }: PostadresseIFrittFormatValues) => {
	return (
		<FrittFormatAdresse
			path={path}
			extraFields={(p: string) => (
				<FormSelect
					name={`${p}.postnummer`}
					label="Postnummer"
					kodeverk={AdresseKodeverk.Postnummer}
					size="large"
				/>
			)}
		/>
	)
}
