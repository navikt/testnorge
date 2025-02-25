import React, { useContext, useState } from 'react'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useToggle } from 'react-use'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Mal, useDollyOrganisasjonMaler } from '@/utils/hooks/useMaler'
import { useFormContext } from 'react-hook-form'
import Button from '@/components/ui/button/Button'
import { NavLink } from 'react-router'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { BVOptions } from '@/components/bestillingsveileder/options/options'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

type MalVelgerProps = {
	brukernavn: string
	gruppeId: number
}

export function getBrukerOptions(malbestillinger: Record<string, Mal[]>) {
	return Object.keys(malbestillinger || {}).map((ident) => ({
		value: ident,
		label: ident,
	}))
}

export function getMalOptions(malbestillinger: Record<string, Mal[]>, bruker: string) {
	if (!malbestillinger || !malbestillinger[bruker]) return []
	return malbestillinger[bruker].map((mal) => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.bestilling, malNavn: mal.malNavn, id: mal.id },
	}))
}

export const MalVelgerOrganisasjon = ({ brukernavn, gruppeId }: MalVelgerProps) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { dollyEnvironments } = useDollyEnvironments()
	const formMethods = useFormContext()
	const { maler, loading } = useDollyOrganisasjonMaler()
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(formMethods.getValues('mal'))

	const brukerOptions = getBrukerOptions(maler)
	const malOptions = getMalOptions(maler, bruker)

	const handleMalChange = (mal: { value: string; label: string; data: any }) => {
		if (mal) {
			opts.mal = mal.data
			const options = BVOptions(opts, gruppeId, dollyEnvironments)
			formMethods.reset(options.initialValues)
			formMethods.setValue('mal', mal.value)
		} else {
			opts.mal = undefined
			formMethods.setValue('mal', undefined)
			formMethods.reset(opts.initialValues)
		}
	}

	const handleMalEnable = () => {
		opts.mal = undefined
		toggleMalAktiv()
		formMethods.setValue('mal', undefined)
		formMethods.reset(opts.initialValues)
	}

	const handleBrukerChange = (event: { value: string }) => {
		setBruker(event.value)
		formMethods.setValue('mal', undefined)
	}

	return (
		<div className="ny-bestilling-form_maler">
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
