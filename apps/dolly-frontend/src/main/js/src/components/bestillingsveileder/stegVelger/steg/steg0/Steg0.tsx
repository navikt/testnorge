import React, { useContext, useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { MalVelgerOrganisasjon } from '@/pages/organisasjoner/MalVelgerOrganisasjon'
import { VelgIdenttype } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgIdenttype'
import {
	getInitialNyIdent,
	getInitialSivilstand,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgGruppe'
import { MalVelgerIdent } from '@/components/bestillingsveileder/startModal/MalVelgerIdent'

const GRUPPE_AVHENGIGE_ATTRIBUTTER = [
	'fullmakt',
	'pdldata.person.vergemaal',
	'pdldata.person.nyident',
	'pdldata.person.sivilstand',
] as const

type AttributeHandlers = {
	[key: string]: () => void
}

const handleAttributeUpdates = (formMethods, opts) => {
	const master = opts.identtype === 'NPID' ? 'PDL' : 'FREG'
	const attributeHandlers: AttributeHandlers = {
		fullmakt: () => {
			const formFullmakt = formMethods.getValues('fullmakt')
			const newFormFullmakt = formFullmakt?.map((fullmakt, _idx) => {
				return {
					...fullmakt,
					nyfullMektig: initialPdlPerson,
					fullmektigsNavn: undefined,
					fullmektig: undefined,
				}
			})
			formMethods.setValue('fullmakt', newFormFullmakt)
		},
		'pdldata.person.vergemaal': () => {
			const formVergemaal = formMethods.getValues('pdldata.person.vergemaal')
			const newFormVergemaal = formVergemaal?.map((vergemaal, _idx) => {
				return {
					...vergemaal,
					nyVergeIdent: initialPdlPerson,
					vergeIdent: undefined,
				}
			})
			formMethods.setValue('pdldata.person.vergemaal', newFormVergemaal)
		},
		'pdldata.person.nyident': () => {
			formMethods.setValue('pdldata.person.nyident', [getInitialNyIdent(master)])
		},
		'pdldata.person.sivilstand': () => {
			formMethods.setValue('pdldata.person.sivilstand', [getInitialSivilstand(master)])
		},
	}

	GRUPPE_AVHENGIGE_ATTRIBUTTER.forEach((attributt) => {
		if (formMethods.getValues(attributt) && attributeHandlers[attributt]) {
			attributeHandlers[attributt]()
		}
	})
}

const Steg0 = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const isOrganisasjon =
		opts.is?.nyOrganisasjon || opts.is?.nyStandardOrganisasjon || opts.is?.nyOrganisasjonFraMal
	const isNyIdent = opts.is?.nyBestilling || opts.is?.nyBestillingFraMal
	const velgGruppeDisabled = opts.is?.leggTil || opts.is?.leggTilPaaGruppe

	const formGruppeId = formMethods.watch('gruppeId')
	const rawGruppeId = formGruppeId || opts?.gruppeId || opts?.gruppe?.id
	const gruppeId = rawGruppeId ? String(rawGruppeId) : ''
	const numericGruppeId = gruppeId ? parseInt(gruppeId) : null

	useEffect(() => {
		if ((!formGruppeId || typeof formGruppeId === 'string') && numericGruppeId) {
			formMethods.setValue('gruppeId', numericGruppeId)
		}
		const contextGruppeId = opts.gruppeId && parseInt(opts.gruppeId)
		if (!numericGruppeId || contextGruppeId === numericGruppeId) {
			return
		}

		opts.gruppeId = numericGruppeId

		handleAttributeUpdates(formMethods, opts)
	}, [numericGruppeId, formMethods, opts])

	const username = currentBruker?.brukernavn || ''
	const brukerId = currentBruker?.brukerId || ''

	return (
		<div className="start-bestilling-modal">
			{!isOrganisasjon && !velgGruppeDisabled && (
				<>
					{isNyIdent && (
						<div className="dolly-panel dolly-panel-open">
							<VelgIdenttype gruppeId={numericGruppeId} />
						</div>
					)}
					{!velgGruppeDisabled && (
						<div className="dolly-panel dolly-panel-open">
							<VelgGruppe formMethods={formMethods} title="Hvilken gruppe vil du bestille til?" />
						</div>
					)}
				</>
			)}
			<div className="dolly-panel dolly-panel-open">
				{isOrganisasjon ? (
					<MalVelgerOrganisasjon brukernavn={username} gruppeId={numericGruppeId} />
				) : (
					<MalVelgerIdent brukerId={brukerId} gruppeId={numericGruppeId} />
				)}
			</div>
		</div>
	)
}

export default Steg0
