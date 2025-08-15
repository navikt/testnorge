import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Attributt } from '../Attributt'
import { AttributtKategori } from '../AttributtKategori'
import {
	getInitialNyIdent,
	getInitialUtenlandskIdentifikasjonsnummer,
} from '@/components/fagsystem/pdlf/form/initialValues'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { identifikasjonAttributter } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/Identifikasjon'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { useFormContext } from 'react-hook-form'

export const IdentifikasjonPanel = ({ stateModifier, formValues }) => {
	const formMethods = useFormContext()
	const sm = stateModifier(IdentifikasjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const harNpid = opts.identtype === 'NPID'
	const erTestnorgePerson = opts?.identMaster === 'PDL'
	const formGruppeId = formMethods.watch('gruppeId')

	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = identer?.filter((ident) => ident.master === 'PDL').length > 0
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe
	const tekstLeggTilPaaGruppe =
		'Støttes ikke for "legg til på alle" i grupper som inneholder personer fra Test-Norge'

	const getIgnoreKeys = () => {
		let ignoreKeys = []
		if (harNpid) {
			ignoreKeys.push('falskIdentitet')
		}
		if (harTestnorgeIdenter && leggTilPaaGruppe) {
			ignoreKeys.push('falskIdentitet', 'utenlandskIdentifikasjonsnummer', 'nyident')
		}
		if (erTestnorgePerson) {
			ignoreKeys.push('falskIdentitet', 'nyident')
		}
		return ignoreKeys
	}

	return (
		<Panel
			heading={IdentifikasjonPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType="identifikasjon"
			startOpen={harValgtAttributt(formValues, identifikasjonAttributter)}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.falskIdentitet}
					disabled={harNpid || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(harNpid && 'Personer med identtype NPID kan ikke ha falsk identitet') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) ||
						''
					}
					vis={!erTestnorgePerson}
				/>
				<Attributt
					attr={sm.attrs.utenlandskIdentifikasjonsnummer}
					disabled={harTestnorgeIdenter && leggTilPaaGruppe}
					title={(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) || ''}
				/>
				<Attributt
					attr={sm.attrs.nyident}
					disabled={harTestnorgeIdenter && leggTilPaaGruppe}
					title={(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) || ''}
					vis={!erTestnorgePerson}
				/>
			</AttributtKategori>
		</Panel>
	)
}

IdentifikasjonPanel.heading = 'Identifikasjon'

IdentifikasjonPanel.initialValues = ({ set, opts, del, has }) => {
	const { identtype, identMaster } = opts
	return {
		falskIdentitet: {
			label: 'Har falsk identitet',
			checked: has('pdldata.person.falskIdentitet'),
			add() {
				set('pdldata.person.falskIdentitet', [
					{
						erFalsk: true,
						kilde: 'Dolly',
						master: 'FREG',
					},
				])
			},
			remove() {
				del('pdldata.person.falskIdentitet')
			},
		},
		utenlandskIdentifikasjonsnummer: {
			label: 'Har utenlandsk ID',
			checked: has('pdldata.person.utenlandskIdentifikasjonsnummer'),
			add: () =>
				set('pdldata.person.utenlandskIdentifikasjonsnummer', [
					getInitialUtenlandskIdentifikasjonsnummer(
						identtype === 'NPID' || identMaster === 'PDL' ? 'PDL' : 'FREG',
					),
				]),
			remove: () => del('pdldata.person.utenlandskIdentifikasjonsnummer'),
		},
		nyident: {
			label: 'Har ny ident',
			checked: has('pdldata.person.nyident'),
			add() {
				set('pdldata.person.nyident', [getInitialNyIdent(identtype === 'NPID' ? 'PDL' : 'FREG')])
			},
			remove() {
				del('pdldata.person.nyident')
			},
		},
	}
}
