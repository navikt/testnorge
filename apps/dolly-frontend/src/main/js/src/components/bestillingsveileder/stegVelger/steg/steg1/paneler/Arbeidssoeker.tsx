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
import * as _ from 'lodash-es'
import { initialValuesArbeidssoekerregisteret } from '@/components/fagsystem/arbeidssoekerregisteret/form/initialValues'

export const ArbeidssoekerPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(ArbeidssoekerPanel.initialValues)

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
				<Attributt attr={sm.attrs.jobboensker} />
				<Attributt attr={sm.attrs.utdanning} />
				<Attributt attr={sm.attrs.fagbrev} />
				<Attributt attr={sm.attrs.arbeidserfaring} />
				<Attributt attr={sm.attrs.annenErfaring} />
				<Attributt attr={sm.attrs.kompetanser} />
				<Attributt attr={sm.attrs.offentligeGodkjenninger} />
				<Attributt attr={sm.attrs.andreGodkjenninger} />
				<Attributt attr={sm.attrs.spraak} />
				<Attributt attr={sm.attrs.foererkort} />
				<Attributt attr={sm.attrs.kurs} />
				<Attributt attr={sm.attrs.sammendrag} />
			</AttributtKategori>
		</Panel>
	)
}

ArbeidssoekerPanel.heading = 'Arbeidssøker'

ArbeidssoekerPanel.initialValues = ({ set, setMulti, opts, del, has, values }) => {
	const { personFoerLeggTil } = opts
	const personFoerLeggTilHarHjemmel = personFoerLeggTil?.arbeidsplassenCV?.harHjemmel

	const hjemmel = () => {
		if (has('arbeidsplassenCV.harHjemmel')) {
			return values('arbeidsplassenCV.harHjemmel')
		} else if (_.isBoolean(personFoerLeggTilHarHjemmel)) {
			return personFoerLeggTilHarHjemmel
		}
		return true
	}

	const fjernHjemmelPath = (fjernPath: string) => {
		const selected = values()
		if (
			selected?.arbeidsplassenCV &&
			Object.keys(selected?.arbeidsplassenCV)?.length === 2 &&
			_.has(selected, fjernPath) &&
			_.has(selected, 'arbeidsplassenCV.harHjemmel')
		) {
			return [fjernPath, 'arbeidsplassenCV.harHjemmel']
		}
		return fjernPath
	}

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
				setMulti(
					['arbeidsplassenCV.jobboensker', initialJobboenskerVerdier],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.jobboensker'))
			},
		},
		utdanning: {
			label: 'Har utdanning',
			checked: has('arbeidsplassenCV.utdanning'),
			add() {
				setMulti(
					['arbeidsplassenCV.utdanning', [initialUtdanningVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.utdanning'))
			},
		},
		fagbrev: {
			label: 'Har fagbrev',
			checked: has('arbeidsplassenCV.fagbrev'),
			add() {
				setMulti(
					['arbeidsplassenCV.fagbrev', [initialFagbrevVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.fagbrev'))
			},
		},
		arbeidserfaring: {
			label: 'Har arbeidserfaring',
			checked: has('arbeidsplassenCV.arbeidserfaring'),
			add() {
				setMulti(
					['arbeidsplassenCV.arbeidserfaring', [initialArbeidserfaringVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.arbeidserfaring'))
			},
		},
		annenErfaring: {
			label: 'Har andre erfaringer',
			checked: has('arbeidsplassenCV.annenErfaring'),
			add() {
				setMulti(
					['arbeidsplassenCV.annenErfaring', [initialAnnenErfaringVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.annenErfaring'))
			},
		},
		kompetanser: {
			label: 'Har kompetanser',
			checked: has('arbeidsplassenCV.kompetanser'),
			add() {
				setMulti(
					['arbeidsplassenCV.kompetanser', [initialKompetanserVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.kompetanser'))
			},
		},
		offentligeGodkjenninger: {
			label: 'Har offentlige godkjenninger',
			checked: has('arbeidsplassenCV.offentligeGodkjenninger'),
			add() {
				setMulti(
					['arbeidsplassenCV.offentligeGodkjenninger', [initialOffentligeGodkjenningerVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.offentligeGodkjenninger'))
			},
		},
		andreGodkjenninger: {
			label: 'Har andre godkjenninger',
			checked: has('arbeidsplassenCV.andreGodkjenninger'),
			add() {
				setMulti(
					['arbeidsplassenCV.andreGodkjenninger', [initialAndreGodkjenningerVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.andreGodkjenninger'))
			},
		},
		spraak: {
			label: 'Har språk',
			checked: has('arbeidsplassenCV.spraak'),
			add() {
				setMulti(
					['arbeidsplassenCV.spraak', [initialSpraakVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.spraak'))
			},
		},
		foererkort: {
			label: 'Har førerkort',
			checked: has('arbeidsplassenCV.foererkort'),
			add() {
				setMulti(
					['arbeidsplassenCV.foererkort', [initialFoererkortVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.foererkort'))
			},
		},
		kurs: {
			label: 'Har kurs',
			checked: has('arbeidsplassenCV.kurs'),
			add() {
				setMulti(
					['arbeidsplassenCV.kurs', [initialKursVerdier]],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.kurs'))
			},
		},
		sammendrag: {
			label: 'Har sammendrag',
			checked: has('arbeidsplassenCV.sammendrag'),
			add() {
				setMulti(
					['arbeidsplassenCV.sammendrag', initialSammendragVerdi],
					['arbeidsplassenCV.harHjemmel', hjemmel()],
				)
			},
			remove() {
				del(fjernHjemmelPath('arbeidsplassenCV.sammendrag'))
			},
		},
	}
}
