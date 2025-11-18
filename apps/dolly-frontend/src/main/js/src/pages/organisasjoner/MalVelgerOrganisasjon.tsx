import React, { useEffect, useRef, useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useToggle } from 'react-use'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Mal, useDollyOrganisasjonMaler } from '@/utils/hooks/useMaler'
import { useFormContext } from 'react-hook-form'
import Button from '@/components/ui/button/Button'
import { NavLink } from 'react-router'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type MalVelgerProps = {
	brukernavn: string
	gruppeId: number | undefined
}

export function getBrukerOptions(malbestillinger: Record<string, Mal[]> | undefined) {
	return Object.keys(malbestillinger || {}).map((ident) => ({
		value: ident,
		label: ident,
	}))
}

export function getMalOptions(malbestillinger: Record<string, Mal[]> | undefined, bruker: string) {
	if (!malbestillinger || !malbestillinger[bruker]) return []
	return malbestillinger[bruker].map((mal) => ({
		value: mal.id,
		label: mal.malNavn,
		data: {
			bestilling: (mal as any).malBestilling || (mal as any).bestilling,
			malNavn: mal.malNavn,
			id: mal.id,
		},
	}))
}

export const MalVelgerOrganisasjon = ({ brukernavn, gruppeId: _gruppeId }: MalVelgerProps) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const formMethods = useFormContext()
	const { maler, loading } = useDollyOrganisasjonMaler()
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(formMethods.getValues('mal') || false)
	const prevMalIdRef = useRef<string | undefined>(undefined)

	const brukerOptions = getBrukerOptions(maler as any)
	const malOptions = getMalOptions(maler as any, bruker)

	const handleMalChange = (mal: { value: string; label: string; data: any }) => {
		if (mal) {
			opts.updateContext && opts.updateContext({ mal: mal.data })
			formMethods.setValue('mal', mal.value)
		} else {
			opts.updateContext && opts.updateContext({ mal: undefined })
			formMethods.setValue('mal', null)
		}
	}

	const handleMalEnable = () => {
		opts.updateContext && opts.updateContext({ mal: undefined })
		toggleMalAktiv()
		formMethods.setValue('mal', null)
	}

	useEffect(() => {
		const currentMalId = opts.mal?.id

		if (currentMalId && currentMalId !== prevMalIdRef.current) {
			formMethods.reset(opts.initialValues)
			prevMalIdRef.current = currentMalId
		} else if (!currentMalId && prevMalIdRef.current) {
			prevMalIdRef.current = undefined
		}
	}, [opts.mal?.id])

	const handleBrukerChange = (event: { value: string }) => {
		setBruker(event.value)
		formMethods.setValue('mal', null)
	}

	return (
		<div className="ny-bestilling-form_input">
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
					label="Bruker"
					isLoading={loading}
					options={brukerOptions}
					size="medium"
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
					isLoading={loading}
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
		</div>
	)
}
