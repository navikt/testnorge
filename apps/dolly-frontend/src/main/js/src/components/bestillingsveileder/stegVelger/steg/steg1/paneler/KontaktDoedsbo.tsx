import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialKontaktinfoForDoedebo } from '@/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { doedsboAttributt } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { useFormContext } from 'react-hook-form'

export const KontaktDoedsboPanel = ({ stateModifier, formValues, testnorgeIdent }) => {
	const formMethods = useFormContext()
	const sm = stateModifier(KontaktDoedsboPanel.initialValues)
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formGruppeId = formMethods.watch('gruppeId')

	const gruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const harTestnorgeIdenter = (identer?.filter((ident) => ident.master === 'PDL')?.length ?? 0) > 0

	const npidPerson = opts?.identtype === 'NPID'
	const leggTilPaaGruppe = !!opts?.leggTilPaaGruppe
	const tekstLeggTilPaaGruppe =
		'Støttes ikke for "legg til på alle" i grupper som inneholder personer fra Test-Norge'

	if (testnorgeIdent) {
		return null
	}

	const getIgnoreKeys = () => {
		if (npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)) {
			return ['kontaktinformasjonForDoedsbo']
		}
		return []
	}

	return (
		<Panel
			heading={KontaktDoedsboPanel.heading}
			checkAttributeArray={() => sm.batchAdd(getIgnoreKeys())}
			uncheckAttributeArray={sm.batchRemove}
			iconType="doedsbo"
			startOpen={harValgtAttributt(formValues, [doedsboAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.kontaktinformasjonForDoedsbo}
					disabled={npidPerson || (harTestnorgeIdenter && leggTilPaaGruppe)}
					title={
						(npidPerson && 'Ikke tilgjengelig for personer med identtype NPID') ||
						(harTestnorgeIdenter && leggTilPaaGruppe && tekstLeggTilPaaGruppe) ||
						''
					}
				/>
			</AttributtKategori>
		</Panel>
	)
}

KontaktDoedsboPanel.heading = 'Kontaktinformasjon for dødsbo'

KontaktDoedsboPanel.initialValues = ({ set, del, has }) => ({
	kontaktinformasjonForDoedsbo: {
		label: 'Har kontaktinformasjon for dødsbo',
		checked: has('pdldata.person.kontaktinformasjonForDoedsbo'),
		add() {
			set('pdldata.person.kontaktinformasjonForDoedsbo', [initialKontaktinfoForDoedebo])
		},
		remove() {
			del('pdldata.person.kontaktinformasjonForDoedsbo')
		},
	},
})
