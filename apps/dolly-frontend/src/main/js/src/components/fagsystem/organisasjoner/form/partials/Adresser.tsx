import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { UseFormReturn } from 'react-hook-form/dist/types'

type AdresserProps = {
	formMethods: UseFormReturn
	path: string
}

const hjelpetekstAdresser =
	'For å få generert en gyldig norsk adresse kan du velge å fylle ut postnummer, kommunenummer, eller ingen ting. Det vil opprettes en gyldig adresse på grunnlag av det du har fylt ut, og har du ikke fylt ut noe blir adressen en tilfeldig gyldig adresse.'

export const Adresser = ({ formMethods, path }: AdresserProps) => {
	const landForretningsadresse = formMethods.watch(`${path}.forretningsadresse.landkode`)
	const landPostadresse = formMethods.watch(`${path}.postadresse.landkode`)

	const handleLandChange = (adressePath: string) => {
		formMethods.setValue(`${adressePath}.postnr`, '')
		formMethods.setValue(`${adressePath}.kommunenr`, '')
		formMethods.setValue(`${adressePath}.poststed`, '')
	}

	return (
		<>
			<Kategori
				flexRow={true}
				title="Forretningsadresse"
				vis="organisasjon.forretningsadresse"
				hjelpetekst={hjelpetekstAdresser}
			>
				<FormSelect
					name={`${path}.forretningsadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
					afterChange={() => handleLandChange(`${path}.forretningsadresse`)}
					isClearable={false}
					size="large"
				/>

				{landForretningsadresse === 'NO' ? (
					<>
						<FormSelect
							name={`${path}.forretningsadresse.postnr`}
							label={'Postnummer/sted'}
							kodeverk={AdresseKodeverk.Postnummer}
							size="large"
						/>
						<FormSelect
							name={`${path}.forretningsadresse.kommunenr`}
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
						/>
					</>
				) : (
					<>
						<FormTextInput name={`${path}.forretningsadresse.postnr`} label="Postnummer" />
						<FormTextInput name={`${path}.forretningsadresse.poststed`} label="Poststed" />
					</>
				)}

				<FormTextInput
					name={`${path}.forretningsadresse.adresselinjer[0]`}
					label="Adresselinje 1"
				/>
				<FormTextInput
					name={`${path}.forretningsadresse.adresselinjer[1]`}
					label="Adresselinje 2"
				/>
				<FormTextInput
					name={`${path}.forretningsadresse.adresselinjer[2]`}
					label="Adresselinje 3"
				/>
			</Kategori>

			<Kategori
				title="Postadresse"
				vis="organisasjon.postadresse"
				hjelpetekst={hjelpetekstAdresser}
				flexRow={true}
			>
				<FormSelect
					name={`${path}.postadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
					afterChange={() => handleLandChange(`${path}.postadresse`)}
					isClearable={false}
					size="large"
				/>
				{landPostadresse === 'NO' ? (
					<>
						<FormSelect
							name={`${path}.postadresse.postnr`}
							label={'Postnummer/sted'}
							kodeverk={AdresseKodeverk.Postnummer}
							size="large"
						/>
						<FormSelect
							name={`${path}.postadresse.kommunenr`}
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
						/>
					</>
				) : (
					<>
						<FormTextInput name={`${path}.postadresse.postnr`} label="Postnummer" />
						<FormTextInput name={`${path}.postadresse.poststed`} label="Poststed" />
					</>
				)}
				<FormTextInput name={`${path}.postadresse.adresselinjer[0]`} label="Adresselinje 1" />
				<FormTextInput name={`${path}.postadresse.adresselinjer[1]`} label="Adresselinje 2" />
				<FormTextInput name={`${path}.postadresse.adresselinjer[2]`} label="Adresselinje 3" />
			</Kategori>
		</>
	)
}
