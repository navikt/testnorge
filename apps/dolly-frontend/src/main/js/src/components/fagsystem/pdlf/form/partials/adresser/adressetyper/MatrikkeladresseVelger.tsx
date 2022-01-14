import React from 'react'
import _get from 'lodash/get'
import { initialMatrikkeladresse } from '~/components/fagsystem/pdlf/form/initialValues'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Matrikkeladresse'
import { MatrikkeladresseTilfeldig } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/MatrikkeladresseTilfeldig'

export const MatrikkeladresseVelger = ({ formikBag, path }) => {
	const matrikkeladresseValg = {
		TILFELDIG: 'TILFELDIG',
		DETALJERT: 'DETALJERT',
	}

	const matrikkeladresseType = _get(formikBag.values, `${path}.matrikkeladresseType`) || null

	const handleRadioChange = (valg) => {
		formikBag.setFieldValue(path, {
			...initialMatrikkeladresse,
			matrikkeladresseType: valg.target.value,
		})
	}

	return (
		<div
			className="flexbox--full-width"
			key={`matrikkeladresse_${path}`}
			style={{ marginBottom: '10px' }}
		>
			<RadioPanelGruppe
				name={`matrikkeladresse_${path}`}
				key={`matrikkeladresse_${path}`}
				legend="Hva slags matrikkeladresse vil du opprette?"
				radios={[
					{
						label: 'Tilfeldig matrikkeladresse ...',
						value: matrikkeladresseValg.TILFELDIG,
						id: `tilfeldig_matrikkel_${path}`,
					},
					{
						label: 'Detaljert matrikkeladresse ...',
						value: matrikkeladresseValg.DETALJERT,
						id: `detaljert_matrikkel_${path}`,
					},
				]}
				checked={matrikkeladresseType}
				onChange={(valg) => handleRadioChange(valg)}
			/>

			{matrikkeladresseType === matrikkeladresseValg.TILFELDIG && (
				<MatrikkeladresseTilfeldig formikBag={formikBag} path={path} />
			)}

			{matrikkeladresseType === matrikkeladresseValg.DETALJERT && (
				<Matrikkeladresse formikBag={formikBag} path={path} />
			)}
		</div>
	)
}
