import React, { useContext, useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Alert } from '@navikt/ds-react'
import { useToggle } from 'react-use'
import * as _ from 'lodash-es'
import { tpsfAttributter } from '@/components/bestillingsveileder/utils'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Mal, useMalbestillingBruker, useMalbestillingOversikt } from '@/utils/hooks/useMaler'
import { BVOptions } from '@/components/bestillingsveileder/options/options'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
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
	if (!malbestillinger || malbestillinger?.length < 1) return []
	return malbestillinger.map((mal) => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.malBestilling, malNavn: mal.malNavn, id: mal.id },
	}))
}

export const MalVelgerIdent = ({ brukerId, gruppeId }: MalVelgerProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { brukere, loading: loadingBrukere } = useMalbestillingOversikt()
	const [bruker, setBruker] = useState(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const { maler, loading: loadingMaler } = useMalbestillingBruker(bruker)

	const { dollyEnvironments } = useDollyEnvironments()
	const [malAktiv, toggleMalAktiv] = useToggle(formMethods.getValues('mal') || false)

	const brukerOptions = getBrukerOptions(brukere)
	const malOptions = getMalOptions(maler)

	const handleMalChange = (mal: { value: string; label: string; data: any }) => {
		if (mal) {
			opts.mal = mal.data
			const options = BVOptions(opts, gruppeId, dollyEnvironments)
			formMethods.reset(options.initialValues)
			formMethods.setValue('mal', mal.value)
			formMethods.setValue('gruppeId', gruppeId)
		} else {
			opts.mal = undefined
			const options = BVOptions(opts, gruppeId, dollyEnvironments)
			formMethods.reset(options.initialValues)
			formMethods.setValue('mal', null)
			formMethods.setValue('gruppeId', gruppeId)
		}
	}

	const handleMalEnable = () => {
		opts.mal = undefined
		const options = BVOptions(opts, gruppeId, dollyEnvironments)
		toggleMalAktiv()
		formMethods.reset(options.initialValues)
		formMethods.setValue('mal', null)
		formMethods.setValue('gruppeId', gruppeId)
	}

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
				wrapperSize={'none'}
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
