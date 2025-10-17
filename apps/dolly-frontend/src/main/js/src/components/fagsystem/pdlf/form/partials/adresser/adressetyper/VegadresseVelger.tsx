import { initialVegadresse } from '@/components/fagsystem/pdlf/form/initialValues'
import { Vegadresse } from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Radio, RadioGroup } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface VegadressevelgerValues {
	formMethods: UseFormReturn
	path: string
}

export const VegadresseVelger = ({ formMethods, path }: VegadressevelgerValues) => {
	const vegadresseValg = {
		POSTNUMMER: 'POSTNUMMER',
		KOMMUNENUMMER: 'KOMMUNENUMMER',
		BYDELSNUMMER: 'BYDELSNUMMER',
		DETALJERT: 'DETALJERT',
	}

	const vegadresseType = formMethods.watch(`${path}.vegadresseType`) || null

	const handleRadioChange = (valg: string) => {
		formMethods.setValue(path, { ...initialVegadresse, vegadresseType: valg })
		formMethods.trigger(path)
	}

	return (
		<div
			className="flexbox--full-width"
			key={`vegadresse_${path}`}
			style={{ marginBottom: '10px' }}
		>
			<RadioGroup
				name={`vegadresse_${path}`}
				size={'small'}
				key={`vegadresse_${path}`}
				value={vegadresseType}
				onChange={(valg) => handleRadioChange(valg)}
				legend="Hva slags vegadresse vil du opprette?"
			>
				<Radio id={`postnummer_${path}`} value={vegadresseValg.POSTNUMMER}>
					Tilfeldig vegadresse basert på postnummer ...
				</Radio>
				<Radio id={`bydelsnummer_${path}`} value={vegadresseValg.BYDELSNUMMER}>
					Tilfeldig vegadresse basert på bydelsnummer ...
				</Radio>
				<Radio id={`kommunenummer_${path}`} value={vegadresseValg.KOMMUNENUMMER}>
					Tilfeldig vegadresse basert på kommunenummer ...
				</Radio>
				<Radio id={`detaljert_${path}`} value={vegadresseValg.DETALJERT}>
					Detaljert vegadresse ...
				</Radio>
			</RadioGroup>

			{vegadresseType === vegadresseValg.POSTNUMMER && (
				<FormSelect
					name={`${path}.postnummer`}
					label="Postnummer"
					kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.BYDELSNUMMER && (
				<FormSelect
					name={`${path}.bydelsnummer`}
					label="Bydelsnummer"
					kodeverk={GtKodeverk.BYDEL}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.KOMMUNENUMMER && (
				<FormSelect
					name={`${path}.kommunenummer`}
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.DETALJERT && (
				<Vegadresse formMethods={formMethods} path={path} />
			)}
		</div>
	)
}
