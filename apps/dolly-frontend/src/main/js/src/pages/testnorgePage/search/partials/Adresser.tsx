import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { RadioGroupOptions } from '@/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
import { yesNoOptions } from '@/pages/testnorgePage/utils'
import { AdvancedOptions } from '@/pages/testnorgePage/search/advancedOptions/AdvancedOptions'
import { UseFormReturn } from 'react-hook-form/dist/types'

type AdresserProps = {
	formMethods: UseFormReturn
}

const paths = {
	adressebeskyttelse: 'identifikasjon.adressebeskyttelse',
	borINorge: 'adresser.bostedsadresse.borINorge',
	postnummer: 'adresser.bostedsadresse.postnummer',
	bydelsnummer: 'adresser.bostedsadresse.bydelsnummer',
	kommunenummer: 'adresser.bostedsadresse.kommunenummer',
	hPostnummer: 'adresser.bostedsadresse.historiskPostnummer',
	hBydelsnummer: 'adresser.bostedsadresse.historiskBydelsnummer',
	hKommunenummer: 'adresser.bostedsadresse.historiskKommunenummer',
	utenlandsk: 'adresser.harUtenlandskAdresse',
	kontakt: 'adresser.harKontaktadresse',
	opphold: 'adresser.harOppholdsadresse',
}

const adressebeskyttelseOptions = [
	{ value: 'FORTROLIG', label: 'Fortrolig' },
	{ value: 'STRENGT_FORTROLIG', label: 'Strengt fortrolig' },
	{ value: 'INGEN', label: 'Ingen' },
]

export const Adresser = ({ formMethods }: AdresserProps) => {
	const bostedOptions = [
		{ value: 'Y', label: 'Ja' },
		{
			value: 'N',
			label: 'Nei',
			disabled:
				formMethods.watch(paths.kommunenummer) ||
				formMethods.watch(paths.postnummer) ||
				formMethods.watch(paths.bydelsnummer),
		},
	]
	return (
		<section>
			<div className="options-title-bold">Adressebeskyttelse</div>
			<FormSelect
				name={paths.adressebeskyttelse}
				label="Velg type"
				options={adressebeskyttelseOptions}
				size="medium"
			/>
			<div className="options-title-bold">Bostedsadresse</div>
			<RadioGroupOptions
				formMethods={formMethods}
				name={'Har bostedsadresse i Norge'}
				path={paths.borINorge}
				options={bostedOptions}
			/>
			<FormSelect
				name={paths.postnummer}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				isDisabled={formMethods.watch(paths.borINorge) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormSelect
				name={paths.bydelsnummer}
				label="Bydel"
				kodeverk={GtKodeverk.BYDEL}
				isDisabled={formMethods.watch(paths.borINorge) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormSelect
				name={paths.kommunenummer}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				isDisabled={formMethods.watch(paths.borINorge) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<AdvancedOptions>
				<FormSelect
					name={paths.hPostnummer}
					label="Har tidligere hatt postnummer"
					kodeverk={AdresseKodeverk.Postnummer}
					optionHeight={50}
					size="medium"
					info="Velg postnummer tilknyttet tidligere bostedsadresse."
				/>
				<FormSelect
					name={paths.hBydelsnummer}
					label="Har tidligere bodd i bydel"
					kodeverk={GtKodeverk.BYDEL}
					optionHeight={50}
					size="medium"
					info="Velg bydel tilknyttet tidligere bostedsadresse."
				/>
				<FormSelect
					name={paths.hKommunenummer}
					label="Har tidligere bodd i kommune"
					kodeverk={AdresseKodeverk.Kommunenummer}
					optionHeight={50}
					size="medium"
					info="Velg kommunenummer tilknyttet tidligere bostedsadresse."
				/>
			</AdvancedOptions>
			<div className="options-title-bold">Annet</div>
			<RadioGroupOptions
				formMethods={formMethods}
				name={'Har utenlandsk adresse'}
				path={paths.utenlandsk}
				options={yesNoOptions}
			/>
			<RadioGroupOptions
				formMethods={formMethods}
				name={'Har kontaktadresse'}
				path={paths.kontakt}
				options={yesNoOptions}
			/>
			<RadioGroupOptions
				formMethods={formMethods}
				name={'Har oppholdsadresse'}
				path={paths.opphold}
				options={yesNoOptions}
			/>
		</section>
	)
}

export const AdresserPaths = Object.values(paths)
