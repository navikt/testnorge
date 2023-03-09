import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import Panel from '@/components/ui/panel/Panel'
import {
	initialAnnenErfaringVerdier,
	initialArbeidserfaring,
	initialArbeidserfaringVerdier,
	initialCV,
	initialFagbrev,
	initialFagbrevVerdier,
	initialKompetanserVerdier,
	initialUtdanning,
	initialUtdanningVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const ArbeidsplassenPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(ArbeidsplassenPanel.initialValues)
	return (
		<Panel
			heading={ArbeidsplassenPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="cv"
			startOpen={harValgtAttributt(formikBag.values, ['arbeidsplassenCV'])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				{/*<Attributt attr={sm.attrs.arbeidsplassen} />*/}
				<Attributt attr={sm.attrs.utdanning} />
				<Attributt attr={sm.attrs.fagbrev} />
				<Attributt attr={sm.attrs.arbeidserfaring} />
				<Attributt attr={sm.attrs.annenErfaring} />
				<Attributt attr={sm.attrs.kompetanser} />
			</AttributtKategori>
		</Panel>
	)
}

ArbeidsplassenPanel.heading = 'Arbeidsplassen (CV)'

ArbeidsplassenPanel.initialValues = ({ set, del, has }) => ({
	// arbeidsplassen: {
	// 	label: 'Har CV',
	// 	checked: has('arbeidsplassenCV'),
	// 	add() {
	// 		set('arbeidsplassenCV', initialCV)
	// 	},
	// 	remove() {
	// 		del('arbeidsplassenCV')
	// 	},
	// },
	utdanning: {
		label: 'Har utdanning',
		checked: has('arbeidsplassenCV.utdanning'),
		add() {
			set('arbeidsplassenCV.utdanning', [initialUtdanningVerdier])
		},
		remove() {
			del('arbeidsplassenCV.utdanning')
		},
	},
	fagbrev: {
		label: 'Har fagbrev',
		checked: has('arbeidsplassenCV.fagbrev'),
		add() {
			set('arbeidsplassenCV.fagbrev', [initialFagbrevVerdier])
		},
		remove() {
			del('arbeidsplassenCV.fagbrev')
		},
	},
	arbeidserfaring: {
		label: 'Har arbeidserfaring',
		checked: has('arbeidsplassenCV.arbeidserfaring'),
		add() {
			set('arbeidsplassenCV.arbeidserfaring', [initialArbeidserfaringVerdier])
		},
		remove() {
			del('arbeidsplassenCV.arbeidserfaring')
		},
	},
	annenErfaring: {
		label: 'Har andre erfaringer',
		checked: has('arbeidsplassenCV.annenErfaring'),
		add() {
			set('arbeidsplassenCV.annenErfaring', [initialAnnenErfaringVerdier])
		},
		remove() {
			del('arbeidsplassenCV.annenErfaring')
		},
	},
	kompetanser: {
		label: 'Har kompetanser',
		checked: has('arbeidsplassenCV.kompetanser'),
		add() {
			set('arbeidsplassenCV.kompetanser', [initialKompetanserVerdier])
		},
		remove() {
			del('arbeidsplassenCV.kompetanser')
		},
	},
})
