import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'

//TODO dele opp denne?
//TODO få denne til å gjenspeile faktisk skjema
const initialValuesArbeidsforhold = {
	ansettelsesPeriode: {
		fom: '',
		tom: ''
	},
	arbeidsforholdstype: 'ordinaertArbeidsforhold',
	antallTimerForTimeloennet: [
		{
			periode: {
				fom: '',
				tom: ''
			},
			antallTimer: ''
		}
	],
	arbeidsavtale: {
		yrke: '',
		stillingsprosent: '',
		arbeidstidsordning: 'ikkeSkift',
		antallKonverterteTimer: '0',
		// avloenningstype: '',
		avtaltArbeidstimerPerUke: '37,5'
	},
	permisjon: [],
	utenlandsopphold: [],
	arbeidsgiver: {
		aktoertype: '',
		aktoerId: ''
	}
}

export const AaregForm = ({ formikBag }) => (
	<Vis attributt={pathAttrs.kategori.arbeidsforhold}>
		<Panel heading="Arbeidsforhold" hasErrors={panelError(formikBag)} startOpen>
			<ArbeidsforholdForm formikBag={formikBag} initial={initialValuesArbeidsforhold} />
		</Panel>
	</Vis>
)

AaregForm.initialValues = attrs => {
	const initial = {
		aareg: [initialValuesArbeidsforhold]
	}
	return attrs.arbeidsforhold ? initial : {}
}

//TODO validation
