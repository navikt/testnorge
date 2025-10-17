import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React, { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { BarnetilleggForm } from '@/components/fagsystem/uforetrygd/form/partials/BarnetilleggForm'
import { validation } from '@/components/fagsystem/uforetrygd/form/validation'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { useFormContext } from 'react-hook-form'

export const uforetrygdPath = 'pensjonforvalter.uforetrygd'

export const UforetrygdForm = () => {
	const formMethods = useFormContext()
	const saksbehandler = formMethods.watch(`${uforetrygdPath}.saksbehandler`)
	const attesterer = formMethods.watch(`${uforetrygdPath}.attesterer`)

	const [randomSaksbehandlere, setRandomSaksbehandlere] = useState([])
	const [randomAttesterere, setRandomAttesterere] = useState([])
	const { navEnheter } = useNavEnheter()

	useEffect(() => {
		setRandomSaksbehandlere(genererTilfeldigeNavPersonidenter(saksbehandler))
		setRandomAttesterere(genererTilfeldigeNavPersonidenter(attesterer))
	}, [])

	return (
		<Vis attributt={uforetrygdPath}>
			<Panel
				heading="Uføretrygd"
				hasErrors={panelError(uforetrygdPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [uforetrygdPath])}
			>
				<div className="flexbox--flex-wrap">
					<FormDatepicker name={`${uforetrygdPath}.uforetidspunkt`} label="Uføretidspunkt" />
					<Monthpicker
						name={`${uforetrygdPath}.onsketVirkningsDato`}
						label="Ønsket virkningsdato"
						date={formMethods.getValues(`${uforetrygdPath}.onsketVirkningsDato`)}
						handleDateChange={(dato: string) => {
							formMethods.setValue(`${uforetrygdPath}.onsketVirkningsDato`, dato)
							formMethods.trigger(`${uforetrygdPath}`)
						}}
					/>
					<FormTextInput
						name={`${uforetrygdPath}.inntektForUforhet`}
						label="Inntekt før uførhet"
						type="number"
					/>
					<FormTextInput
						name={`${uforetrygdPath}.inntektEtterUforhet`}
						label="Inntekt etter uførhet"
						type="number"
					/>
					<FormSelect
						name={`${uforetrygdPath}.minimumInntektForUforhetType`}
						label="Sats for minimum IFU"
						size="xlarge"
						options={Options('minimumInntektForUforhetType')}
					/>
					<FormTextInput name={`${uforetrygdPath}.uforegrad`} label="Uføregrad" type="number" />
					<FormSelect
						options={randomSaksbehandlere}
						name={`${uforetrygdPath}.saksbehandler`}
						label={'Saksbehandler'}
					/>
					<FormSelect
						options={randomAttesterere}
						name={`${uforetrygdPath}.attesterer`}
						label={'Attesterer'}
					/>
					<FormSelect
						name={`${uforetrygdPath}.navEnhetId`}
						label={'NAV-kontor'}
						size={'xlarge'}
						options={navEnheter}
					/>
				</div>
				<BarnetilleggForm formMethods={formMethods} />
			</Panel>
		</Vis>
	)
}

UforetrygdForm.validation = validation
