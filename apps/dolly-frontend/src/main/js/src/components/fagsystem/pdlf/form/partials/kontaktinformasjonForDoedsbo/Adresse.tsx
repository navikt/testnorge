import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
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
				<Button onClick={setSkjulAdresse} kind="chevron-up">
					SKJUL ADRESSE-VALG
				</Button>
			) : (
				<Button onClick={setVisAdresse} kind="chevron-down">
					VIS ADRESSE-VALG
				</Button>
			)}
			{visAdresse && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormSelect
						name={`${path}.landkode`}
						label="Land"
						kodeverk={AdresseKodeverk.PostadresseLand}
						size="large"
					/>
					<FormTextInput name={`${path}.adresselinje1`} label="Adresselinje 1" />
					<FormTextInput name={`${path}.adresselinje2`} label="Adresselinje 2" />
					{formMethods.watch(`${path}.landkode`) === 'NOR' ? (
						<FormSelect
							name={`${path}.postnummer`}
							label="Postnummer og -sted"
							kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
							afterChange={handleAfterChange}
							size="large"
						/>
					) : (
						<>
							<FormTextInput name={`${path}.postnummer`} label="Postnummer" />
							<FormTextInput name={`${path}.poststedsnavn`} label="Poststed" />
						</>
					)}
				</div>
			)}
		</div>
	)
}
