import React, { useContext, useEffect, useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Alert } from '@navikt/ds-react'
import { useToggle } from 'react-use'
import * as _ from 'lodash-es'
import { tpsfAttributter } from '@/components/bestillingsveileder/utils'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Mal, useMalbestillingBruker, useMalbestillingOversikt } from '@/utils/hooks/useMaler'
import { useFormContext } from 'react-hook-form'
import Button from '@/components/ui/button/Button'
import { NavLink } from 'react-router'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

type MalVelgerProps = {
	brukerId: string
	gruppeId: number | undefined
}

type Brukere = {
	brukerId: string
	brukernavn: string
}

export function getBrukerOptions(brukere: Brukere[] | undefined) {
	return brukere?.map((bruker) => {
		const erTeam = bruker.brukerId.includes('team')
		return {
			value: bruker.brukerId,
			label: bruker.brukernavn + (erTeam ? ' (team)' : ''),
		}
	})
}

export function getMalOptions(malbestillinger: Mal[] | undefined) {
	if (!Array.isArray(malbestillinger) || malbestillinger.length < 1) return []
	return malbestillinger.map((mal) => ({
		value: mal.id,
		label: mal.malNavn,
		data: {
			bestilling: (mal as any).malBestilling ?? (mal as any).bestilling,
			malNavn: mal.malNavn,
			id: mal.id,
		},
	}))
}

export const MalVelgerIdent = ({ brukerId: _brukerId, gruppeId }: MalVelgerProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { brukere, loading: loadingBrukere } = useMalbestillingOversikt()
	const [bruker, setBruker] = useState(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const { maler, loading: loadingMaler } = useMalbestillingBruker(bruker)

	const [malAktiv, toggleMalAktiv] = useToggle(formMethods.getValues('mal') || false)

	const brukerOptions = getBrukerOptions(brukere)
	const malOptions = getMalOptions(maler)

	const handleMalChange = (mal: { value: string; label: string; data: any }) => {
		if (mal) {
			opts.setMal && opts.setMal(mal.data)
			formMethods.setValue('mal', mal.value)
			formMethods.setValue('gruppeId', gruppeId)
			const identtype = _.get(mal.data, 'bestilling.pdldata.opprettNyPerson.identtype')
			const id2032 = _.get(mal.data, 'bestilling.pdldata.opprettNyPerson.id2032')
			if (identtype) formMethods.setValue('pdldata.opprettNyPerson.identtype', identtype)
			if (id2032 !== undefined) formMethods.setValue('pdldata.opprettNyPerson.id2032', id2032)
		} else {
			opts.setMal && opts.setMal(undefined)
			formMethods.setValue('mal', null)
			formMethods.setValue('gruppeId', gruppeId)
		}
	}

	const handleMalEnable = () => {
		opts.setMal && opts.setMal(undefined)
		toggleMalAktiv()
		formMethods.setValue('mal', null)
		formMethods.setValue('gruppeId', gruppeId)
	}

	useEffect(() => {
		if (opts.mal) {
			const identtype = _.get(opts.mal, 'bestilling.pdldata.opprettNyPerson.identtype')
			const id2032 = _.get(opts.mal, 'bestilling.pdldata.opprettNyPerson.id2032')
			if (identtype) formMethods.setValue('pdldata.opprettNyPerson.identtype', identtype)
			if (id2032 !== undefined) formMethods.setValue('pdldata.opprettNyPerson.id2032', id2032)
		}
	}, [opts.mal])

	useEffect(() => {
		if (opts.mal && opts.identtype) {
			const current = formMethods.getValues('pdldata.opprettNyPerson.identtype')
			if (current !== opts.identtype) {
				formMethods.setValue('pdldata.opprettNyPerson.identtype', opts.identtype)
			}
		}
	}, [opts.mal, opts.identtype])

	const handleBrukerChange = (event: { value: string }) => {
		setBruker(event.value)
		formMethods.setValue('mal', null)
	}

	const valgtMalValue = formMethods.watch('mal')
	const valgtMal = malOptions.find((m) => m.value === valgtMalValue)
	const valgtMalTpsf = _.get(valgtMal, 'data.bestilling.tpsf')
	const erTpsfMal = tpsfAttributter.some((a) => _.has(valgtMalTpsf, a))
	const erGammelFullmaktMal = _.has(
		valgtMal,
		'data.bestilling.pdldata.person.fullmakt.[0].omraader.[0]',
	)
	const erGammelAmeldingMal = !!_.get(valgtMal, 'data.bestilling.aareg', [])?.find(
		(a: any) => a?.amelding?.length > 0,
	)
	const erGammelSyntSykemeldingMal = _.has(valgtMal, 'data.bestilling.sykemelding.syntSykemelding')
	const erLignetInntektMal = _.has(valgtMal, 'data.bestilling.sigrunstub')
	const erUtdatertMal =
		erGammelFullmaktMal ||
		erGammelAmeldingMal ||
		erGammelSyntSykemeldingMal ||
		erTpsfMal ||
		erLignetInntektMal

	return (
		<div className="ny-bestilling-form_input">
			<h2>Velg mal</h2>
			<DollyCheckbox
				data-testid={TestComponentSelectors.TOGGLE_MAL}
				name="aktiver-maler"
				onChange={handleMalEnable}
				label="Opprett fra mal"
				checked={malAktiv}
				size={'small'}
				isSwitch
			/>
			<div style={{ marginTop: '10px' }}>
				<DollySelect
					name="zIdent"
					label="Bruker/team"
					isLoading={loadingBrukere}
					options={brukerOptions}
					size="large"
					onChange={handleBrukerChange}
					value={bruker}
					isClearable={false}
					isDisabled={!malAktiv}
				/>
				<FormSelect
					data-testid={TestComponentSelectors.SELECT_MAL}
					name="mal"
					label="Maler"
					onChange={handleMalChange}
					isLoading={loadingMaler}
					options={malOptions}
					size="grow"
					isDisabled={!malAktiv}
					value={valgtMalValue}
				/>
			</div>
			<div className="mal-admin">
				<Button kind="maler" fontSize={'1.2rem'}>
					<NavLink to="/minside">Administrer maler</NavLink>
				</Button>
			</div>
			{erUtdatertMal && (
				<Alert variant={'warning'} size={'small'} style={{ width: '97%', marginBottom: '10px' }}>
					Denne malen er utdatert, og vil muligens ikke fungere som den skal.
				</Alert>
			)}
		</div>
	)
}
