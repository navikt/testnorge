import React, { useState } from 'react'
import * as _ from 'lodash-es'
import styled from 'styled-components'
import { FormikProps } from 'formik'
import { bottom } from '@popperjs/core'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { Radio, RadioGroup } from '@navikt/ds-react'

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

const TestpersonValg = styled(RadioGroup)`
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
	'Om ikke lenge kommer Dolly til å gå fra å opprette personer som har ekte identifikasjonsnummer til å kun opprette ' +
	'personer med syntetisk identifikasjonsnummer. Frem til det vil det være mulig å velge selv om man ønsker å opprette ' +
	'personene med standard eller syntetisk identifikasjonsnummer. Siden syntetisk identifikasjonsnummer en dag kommer ' +
	'til å bli den nye standarden oppfordrer vi alle til å ta dette i bruk allerede nå.'

const syntetiskePaths = {
	forelderBarnRelasjon: 'nyRelatertPerson.syntetisk',
	foreldreansvar: 'nyAnsvarlig.syntetisk',
	fullmakt: 'nyFullmektig.syntetisk',
	kontaktinformasjonForDoedsbo: 'personSomKontakt.nyKontaktperson.syntetisk',
	nyident: 'syntetisk',
	sivilstand: 'nyRelatertPerson.syntetisk',
	vergemaal: 'nyVergeIdent.syntetisk',
}

export const IdentVelger = ({ formikBag }: Form) => {
	const [type, setType] = useState(IdentType.SYNTETISK)

	const handleIdentTypeChange = (value: IdentType) => {
		setType(value)
		const erSyntetisk = value === IdentType.SYNTETISK
		formikBag.setFieldValue(`pdldata.opprettNyPerson.syntetisk`, erSyntetisk)

		for (const [key, value] of Object.entries(syntetiskePaths)) {
			const items = _.get(formikBag.values, `pdldata.person.${key}`)
			if (items !== undefined && !items.isEmpty) {
				for (let i = 0; i < items.length; i++) {
					const path = `pdldata.person.${key}[${i}].${value}`
					if (_.get(formikBag.values, path) !== undefined) {
						formikBag.setFieldValue(path, erSyntetisk)
					}
				}
			}
		}
	}

	return (
		<IdentVelgerField>
			<Tittel>
				<h2>Velg type person</h2>
				<Hjelpetekst placement={bottom}>{informasjonstekst}</Hjelpetekst>
			</Tittel>

			<TestpersonValg
				value={type}
				size={'small'}
				legend={''}
				name="pdldata.opprettNyPerson.syntetisk"
				onChange={(value: IdentType) => handleIdentTypeChange(value)}
			>
				<Radio value={IdentType.SYNTETISK}>NAV syntetisk </Radio>
				<Radio value={IdentType.STANDARD}>Standard</Radio>
			</TestpersonValg>
		</IdentVelgerField>
	)
}
