import React from 'react'
import _get from 'lodash/get'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdForm } from './partials/arbeidsforholdForm'

const initialValuesArbeidsforhold = {
	ansettelsesPeriode: {
		fom: new Date(new Date().setFullYear(new Date().getFullYear() - 20)),
		tom: null
	},
	arbeidsforholdstype: 'ordinaertArbeidsforhold',
	arbeidsgiver: {
		aktoertype: '',
		aktoerId: ''
	},
	arbeidsavtale: {
		yrke: '',
		stillingsprosent: 100,
		endringsdatoStillingsprosent: null,
		arbeidstidsordning: 'ikkeSkift',
		antallKonverterteTimer: 0,
		avtaltArbeidstimerPerUke: 37.5
	},
	antallTimerForTimeloennet: [],
	permisjon: [],
	utenlandsopphold: []
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

AaregForm.validation = validation
