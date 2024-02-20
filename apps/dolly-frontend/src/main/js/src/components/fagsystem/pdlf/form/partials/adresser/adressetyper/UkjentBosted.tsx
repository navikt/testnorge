import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'

interface UkjentBostedValues {
	path: string
}

export const UkjentBosted = ({ path }: UkjentBostedValues) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect
				name={`${path}.bostedskommune`}
				label="Bostedskommune"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
			/>
		</div>
	)
}
