import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'
import Panel from '@/components/ui/panel/Panel'
import {
	initialAndreGodkjenningerVerdier,
	initialAnnenErfaringVerdier,
	initialArbeidserfaringVerdier,
	initialFagbrevVerdier,
	initialFoererkortVerdier,
	initialJobboenskerVerdier,
	initialKompetanserVerdier,
	initialKursVerdier,
	initialOffentligeGodkjenningerVerdier,
	initialSammendragVerdi,
	initialSpraakVerdier,
	initialUtdanningVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { initialValuesArbeidssoekerregisteret } from '@/components/fagsystem/arbeidssoekerregisteret/form/initialValues'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useContext } from 'react'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const ArbeidssoekerPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(ArbeidssoekerPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const cvTimeout = getTimeoutAttr('ARBEIDSPLASSENCV', opts)
	return (
		<Panel
			heading={ArbeidssoekerPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="cv"
			startOpen={harValgtAttributt(formValues, ['arbeidsplassenCV', 'arbeidssoekerregisteret'])}
		>
			<AttributtKategori title="Arbeidssøkerregisteret" attr={sm.attrs}>
				<Attributt attr={sm.attrs.arbeidssoekerregisteret} />
			</AttributtKategori>
			<AttributtKategori title="Nav CV" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.jobboensker}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.utdanning}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt attr={sm.attrs.fagbrev} disabled={cvTimeout.disabled} title={cvTimeout.title} />
				<Attributt
					attr={sm.attrs.arbeidserfaring}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.annenErfaring}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.kompetanser}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.offentligeGodkjenninger}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.andreGodkjenninger}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt attr={sm.attrs.spraak} disabled={cvTimeout.disabled} title={cvTimeout.title} />
				<Attributt
					attr={sm.attrs.foererkort}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
				<Attributt attr={sm.attrs.kurs} disabled={cvTimeout.disabled} title={cvTimeout.title} />
				<Attributt
					attr={sm.attrs.sammendrag}
					disabled={cvTimeout.disabled}
					title={cvTimeout.title}
				/>
			</AttributtKategori>
		</Panel>
	)
}

ArbeidssoekerPanel.heading = 'Arbeidssøker'

ArbeidssoekerPanel.initialValues = ({ set, del, has }: any) => {
	return {
		arbeidssoekerregisteret: {
			label: 'Er arbeidssøker',
			checked: has('arbeidssoekerregisteret'),
			add() {
				set('arbeidssoekerregisteret', initialValuesArbeidssoekerregisteret)
			},
			remove() {
				del('arbeidssoekerregisteret')
			},
		},
		jobboensker: {
			label: 'Har jobbønsker',
			checked: has('arbeidsplassenCV.jobboensker'),
			add() {
				set('arbeidsplassenCV.jobboensker', initialJobboenskerVerdier)
			},
			remove() {
				del('arbeidsplassenCV.jobboensker')
			},
		},
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
		offentligeGodkjenninger: {
			label: 'Har offentlige godkjenninger',
			checked: has('arbeidsplassenCV.offentligeGodkjenninger'),
			add() {
				set('arbeidsplassenCV.offentligeGodkjenninger', [initialOffentligeGodkjenningerVerdier])
			},
			remove() {
				del('arbeidsplassenCV.offentligeGodkjenninger')
			},
		},
		andreGodkjenninger: {
			label: 'Har andre godkjenninger',
			checked: has('arbeidsplassenCV.andreGodkjenninger'),
			add() {
				set('arbeidsplassenCV.andreGodkjenninger', [initialAndreGodkjenningerVerdier])
			},
			remove() {
				del('arbeidsplassenCV.andreGodkjenninger')
			},
		},
		spraak: {
			label: 'Har språk',
			checked: has('arbeidsplassenCV.spraak'),
			add() {
				set('arbeidsplassenCV.spraak', [initialSpraakVerdier])
			},
			remove() {
				del('arbeidsplassenCV.spraak')
			},
		},
		foererkort: {
			label: 'Har førerkort',
			checked: has('arbeidsplassenCV.foererkort'),
			add() {
				set('arbeidsplassenCV.foererkort', [initialFoererkortVerdier])
			},
			remove() {
				del('arbeidsplassenCV.foererkort')
			},
		},
		kurs: {
			label: 'Har kurs',
			checked: has('arbeidsplassenCV.kurs'),
			add() {
				set('arbeidsplassenCV.kurs', [initialKursVerdier])
			},
			remove() {
				del('arbeidsplassenCV.kurs')
			},
		},
		sammendrag: {
			label: 'Har sammendrag',
			checked: has('arbeidsplassenCV.sammendrag'),
			add() {
				set('arbeidsplassenCV.sammendrag', initialSammendragVerdi)
			},
			remove() {
				del('arbeidsplassenCV.sammendrag')
			},
		},
	}
}
