import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React, { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import * as _ from 'lodash'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { BarnetilleggForm } from '@/components/fagsystem/uforetrygd/form/partials/BarnetilleggForm'
import { validation } from '@/components/fagsystem/uforetrygd/form/validation'
import { addDays } from 'date-fns'

const uforetrygdPath = 'pensjonforvalter.uforetrygd'

export const UforetrygdForm = ({ formMethods }) => {
	const saksbehandler = _.get(formMethods.getValues(), `${uforetrygdPath}.saksbehandler`)
	const attesterer = _.get(formMethods.getValues(), `${uforetrygdPath}.attesterer`)

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
				hasErrors={panelError(formMethods.formState.errors, uforetrygdPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [uforetrygdPath])}
			>
				<div className="flexbox--flex-wrap">
					<FormikDatepicker
						name={`${uforetrygdPath}.kravFremsattDato`}
						label="Krav fremsatt dato"
					/>
					<FormikDatepicker
						name={`${uforetrygdPath}.onsketVirkningsDato`}
						label="Ønsket virkningsdato"
						minDate={addDays(new Date(), 1)}
					/>
					<FormikDatepicker name={`${uforetrygdPath}.uforetidspunkt`} label="Uføretidspunkt" />
					<FormikTextInput
						name={`${uforetrygdPath}.inntektForUforhet`}
						label="Inntekt før uførhet"
						type="number"
					/>
				</div>
				<BarnetilleggForm formMethods={formMethods} />
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${uforetrygdPath}.minimumInntektForUforhetType`}
						label="Sats for minimum IFU"
						size="xlarge"
						options={Options('minimumInntektForUforhetType')}
					/>

					<FormikTextInput name={`${uforetrygdPath}.uforegrad`} label="Uføregrad" type="number" />
					<FormikSelect
						options={randomSaksbehandlere}
						name={`${uforetrygdPath}.saksbehandler`}
						label={'Saksbehandler'}
					/>
					<FormikSelect
						options={randomAttesterere}
						name={`${uforetrygdPath}.attesterer`}
						label={'Attesterer'}
					/>
					<FormikSelect
						name={`${uforetrygdPath}.navEnhetId`}
						label={'NAV-enhet'}
						size={'xxxlarge'}
						options={navEnheter}
						isClearable={false}
					/>
				</div>
			</Panel>
		</Vis>
	)
}

UforetrygdForm.validation = validation
