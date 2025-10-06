import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import Panel from '@/components/ui/panel/Panel'
import { useFormContext } from 'react-hook-form'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React, { useEffect, useState } from 'react'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

export const alderspensjonNyUttaksgradPath = 'pensjonforvalter.alderspensjonNyUtaksgrad'

export const AlderspensjonNyUttaksgradForm = () => {
	const formMethods = useFormContext()
	const { navEnheter } = useNavEnheter()

	const saksbehandler = formMethods.watch(`${alderspensjonNyUttaksgradPath}.saksbehandler`)
	const attesterer = formMethods.watch(`${alderspensjonNyUttaksgradPath}.attesterer`)

	const [randomAttesterere, setRandomAttesterere] = useState([])
	const [randomSaksbehandlere, setRandomSaksbehandlere] = useState([])

	useEffect(() => {
		setRandomAttesterere(genererTilfeldigeNavPersonidenter(attesterer))
		setRandomSaksbehandlere(genererTilfeldigeNavPersonidenter(saksbehandler))
	}, [])

	return (
		<Vis attributt={alderspensjonNyUttaksgradPath}>
			<Panel
				heading="Alderspensjon ny uttaksgrad"
				hasErrors={panelError(alderspensjonNyUttaksgradPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [alderspensjonNyUttaksgradPath])}
			>
				<div className="flexbox--flex-wrap">
					<FormSelect
						name={`${alderspensjonNyUttaksgradPath}.nyUttaksgrad`}
						label="Uttaksgrad"
						options={Options('apUttaksgrad')}
						isClearable={false}
					/>
					<Monthpicker
						name={`${alderspensjonNyUttaksgradPath}.fom`}
						label="Dato f.o.m."
						date={formMethods.getValues(`${alderspensjonNyUttaksgradPath}.fom`)}
						handleDateChange={(dato: string) => {
							formMethods.setValue(`${alderspensjonNyUttaksgradPath}.fom`, dato, {
								shouldTouch: true,
							})
							formMethods.trigger(`${alderspensjonNyUttaksgradPath}.fom`)
						}}
					/>
					<FormSelect
						options={randomSaksbehandlere}
						name={`${alderspensjonNyUttaksgradPath}.saksbehandler`}
						label={'Saksbehandler'}
					/>
					<FormSelect
						options={randomAttesterere}
						name={`${alderspensjonNyUttaksgradPath}.attesterer`}
						label={'Attesterer'}
					/>
					<FormSelect
						name={`${alderspensjonNyUttaksgradPath}.navEnhetId`}
						label="Nav-kontor"
						size={'xxlarge'}
						options={navEnheter}
					/>
				</div>
			</Panel>
		</Vis>
	)
}
