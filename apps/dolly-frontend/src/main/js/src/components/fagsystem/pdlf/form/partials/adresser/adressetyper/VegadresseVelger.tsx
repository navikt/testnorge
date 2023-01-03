import { initialVegadresse } from '@/components/fagsystem/pdlf/form/initialValues'
import { Vegadresse } from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'
import { Radio, RadioGroup } from '@navikt/ds-react'

interface VegadressevelgerValues {
	formikBag: FormikProps<{}>
	path: string
}

export const VegadresseVelger = ({ formikBag, path }: VegadressevelgerValues) => {
	const vegadresseValg = {
		POSTNUMMER: 'POSTNUMMER',
		KOMMUNENUMMER: 'KOMMUNENUMMER',
		BYDELSNUMMER: 'BYDELSNUMMER',
		DETALJERT: 'DETALJERT',
	}

	const vegadresseType = _.get(formikBag.values, `${path}.vegadresseType`) || null

	const handleRadioChange = (valg: string) => {
		formikBag.setFieldValue(path, { ...initialVegadresse, vegadresseType: valg })
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
				<FormikSelect
					name={`${path}.postnummer`}
					label="Postnummer"
					kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.BYDELSNUMMER && (
				<FormikSelect
					name={`${path}.bydelsnummer`}
					label="Bydelsnummer"
					kodeverk={GtKodeverk.BYDEL}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.KOMMUNENUMMER && (
				<FormikSelect
					name={`${path}.kommunenummer`}
					label="Kommunenummer"
					kodeverk={AdresseKodeverk.Kommunenummer}
					size="large"
					isClearable={false}
				/>
			)}
			{vegadresseType === vegadresseValg.DETALJERT && (
				<Vegadresse formikBag={formikBag} path={path} />
			)}
		</div>
	)
}
