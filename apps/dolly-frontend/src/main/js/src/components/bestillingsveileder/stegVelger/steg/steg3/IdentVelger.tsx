import React, { useState } from 'react'
import _get from 'lodash/get'
import styled from 'styled-components'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import Hjelpetekst from '~/components/hjelpetekst'
import { FormikProps } from 'formik'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'

type Form = {
	formikBag: FormikProps<{}>
}

enum IdentType {
	STANDARD = 'STANDARD',
	SYNTETISK = 'SYNTETISK',
}

const IdentVelgerField = styled.div`
	background-color: white;
	padding: 20px;
	margin-bottom: 20px;
`

const Tittel = styled.div`
	display: flex;
	flex-wrap: wrap;

	h2 {
		margin: 0;
		font-size: 1.4rem;
	}
`

const TestpersonValg = styled(RadioPanelGruppe)`
	legend {
		.offscreen;
	}

	.inputPanelGruppe__inner {
		.inputPanel {
			margin-top: 10px;
		}

		display: flex;
		justify-content: space-between;

		label {
			width: 49%;
			margin-bottom: 0.5rem;
		}
	}
`

const informasjonstekst =
	'Om ikke lenge kommer Dolly til å gå fra å opprette personer som har ekte identifikasjonsnummer til å kun opprette personer med syntetisk identifikasjonsnummer. Frem til det vil det være mulig å velge selv om man ønsker å opprette personene med standard eller syntetisk identifikasjonsnummer. Siden syntetisk identifikasjonsnummer en dag kommer til å bli den nye standarden oppfordrer vi alle til å ta dette i bruk allerede nå.'

export const IdentVelger = ({ formikBag }: Form) => {
	const erArenaBestilling = formikBag.values.hasOwnProperty('arenaforvalter')

	const [type, setType] = useState(
		_get(formikBag.values, `pdldata.opprettNyPerson.syntetisk`) === true
			? IdentType.SYNTETISK
			: IdentType.STANDARD
	)

	const handleIdentTypeChange = (value: IdentType) => {
		setType(value)
		formikBag.setFieldValue(`pdldata.opprettNyPerson.syntetisk`, value === IdentType.SYNTETISK)
	}

	return (
		<IdentVelgerField>
			<Tittel>
				<h2>Velg type person</h2>
				<Hjelpetekst hjelpetekstFor={'Identvelger'}>{informasjonstekst}</Hjelpetekst>
			</Tittel>

			<TestpersonValg
				name="pdldata.opprettNyPerson.syntetisk"
				legend=""
				radios={[
					{ label: 'Standard', value: IdentType.STANDARD },
					{ label: 'NAV syntetisk', value: IdentType.SYNTETISK },
				]}
				checked={type}
				onChange={(e) =>
					handleIdentTypeChange((e.target as HTMLTextAreaElement).value as IdentType)
				}
			/>
		</IdentVelgerField>
	)
}
