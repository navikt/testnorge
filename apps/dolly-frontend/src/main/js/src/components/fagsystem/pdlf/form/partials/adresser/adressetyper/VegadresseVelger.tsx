import React from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import { initialVegadresse } from '~/components/fagsystem/pdlf/form/initialValues'
import { Vegadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Vegadresse'
import { AdresseKodeverk, GtKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { FormikProps } from 'formik'

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

	const vegadresseType = _get(formikBag.values, `${path}.vegadresseType`) || null

	const handleRadioChange = (valg: any) => {
		formikBag.setFieldValue(path, { ...initialVegadresse, vegadresseType: valg.target.value })
	}

	return (
		<div
			className="flexbox--full-width"
			key={`vegadresse_${path}`}
			style={{ marginBottom: '10px' }}
		>
			<RadioPanelGruppe
				name={`vegadresse_${path}`}
				key={`vegadresse_${path}`}
				legend="Hva slags vegadresse vil du opprette?"
				radios={[
					{
						label: 'Tilfeldig vegadresse basert på postnummer ...',
						value: vegadresseValg.POSTNUMMER,
						id: `postnummer_${path}`,
					},
					{
						label: 'Tilfeldig vegadresse basert på bydelsnummer ...',
						value: vegadresseValg.BYDELSNUMMER,
						id: `bydelsnummer_${path}`,
					},
					{
						label: 'Tilfeldig vegadresse basert på kommunenummer ...',
						value: vegadresseValg.KOMMUNENUMMER,
						id: `kommunenummer_${path}`,
					},
					{
						label: 'Detaljert vegadresse ...',
						value: vegadresseValg.DETALJERT,
						id: `detaljert_${path}`,
					},
				]}
				checked={vegadresseType}
				onChange={(valg) => handleRadioChange(valg)}
			/>

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
