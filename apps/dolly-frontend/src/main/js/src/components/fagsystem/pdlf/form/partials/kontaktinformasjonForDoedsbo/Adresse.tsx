import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import * as _ from 'lodash'
import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'

export const Adresse = ({ formMethods, path }) => {
	const [visAdresse, setVisAdresse, setSkjulAdresse] = useBoolean(false)
	const handleAfterChange = (selected) => {
		return formMethods.setValue(`${path}.poststedsnavn`, selected?.data || null)
	}

	return (
		<div className="flexbox--full-width">
			{visAdresse ? (
				<Button onClick={setSkjulAdresse} kind="collapse">
					SKJUL ADRESSE-VALG
				</Button>
			) : (
				<Button onClick={setVisAdresse} kind="chevron-down">
					VIS ADRESSE-VALG
				</Button>
			)}
			{visAdresse && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormikSelect
						name={`${path}.landkode`}
						label="Land"
						kodeverk={AdresseKodeverk.PostadresseLand}
						size="large"
					/>
					<FormikTextInput name={`${path}.adresselinje1`} label="Adresselinje 1" />
					<FormikTextInput name={`${path}.adresselinje2`} label="Adresselinje 2" />
					{_.get(formMethods.getValues(), `${path}.landkode`) === 'NOR' ? (
						<FormikSelect
							name={`${path}.postnummer`}
							label="Postnummer og -sted"
							kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
							afterChange={handleAfterChange}
							size="large"
						/>
					) : (
						<>
							<FormikTextInput name={`${path}.postnummer`} label="Postnummer" />
							<FormikTextInput name={`${path}.poststedsnavn`} label="Poststed" />
						</>
					)}
				</div>
			)}
		</div>
	)
}
