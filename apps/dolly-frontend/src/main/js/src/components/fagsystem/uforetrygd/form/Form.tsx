import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React, { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import * as _ from 'lodash-es'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { BarnetilleggForm } from '@/components/fagsystem/uforetrygd/form/partials/BarnetilleggForm'
import { validation } from '@/components/fagsystem/uforetrygd/form/validation'
import _get from 'lodash/get'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'

const uforetrygdPath = 'pensjonforvalter.uforetrygd'

export const UforetrygdForm = ({ formikBag }) => {
	const saksbehandler = _.get(formikBag.values, `${uforetrygdPath}.saksbehandler`)
	const attesterer = _.get(formikBag.values, `${uforetrygdPath}.attesterer`)

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
				hasErrors={panelError(formikBag, uforetrygdPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formikBag.values, [uforetrygdPath])}
			>
				<div className="flexbox--flex-wrap">
					<FormikDatepicker name={`${uforetrygdPath}.uforetidspunkt`} label="Uføretidspunkt" />
					<FormikDatepicker
						name={`${uforetrygdPath}.kravFremsattDato`}
						label="Krav fremsatt dato"
					/>
					<Monthpicker
						name={`${uforetrygdPath}.onsketVirkningsDato`}
						label="Ønsket virkningsdato"
						date={_get(formikBag.values, `${uforetrygdPath}.onsketVirkningsDato`)}
						handleDateChange={(dato: string) =>
							formikBag.setFieldValue(`${uforetrygdPath}.onsketVirkningsDato`, dato)
						}
					/>
					<FormikTextInput
						name={`${uforetrygdPath}.inntektForUforhet`}
						label="Inntekt før uførhet"
						type="number"
						fastfield="false"
					/>
				</div>
				<BarnetilleggForm formikBag={formikBag} />
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${uforetrygdPath}.minimumInntektForUforhetType`}
						label="Sats for minimum IFU"
						size="xlarge"
						options={Options('minimumInntektForUforhetType')}
					/>

					<FormikTextInput
						name={`${uforetrygdPath}.uforegrad`}
						label="Uføregrad"
						type="number"
						fastfield="false"
					/>
					<FormikSelect
						options={randomSaksbehandlere}
						name={`${uforetrygdPath}.saksbehandler`}
						label={'Saksbehandler'}
						fastfield={false}
					/>
					<FormikSelect
						options={randomAttesterere}
						name={`${uforetrygdPath}.attesterer`}
						label={'Attesterer'}
						fastfield={false}
					/>
					<FormikSelect
						name={`${uforetrygdPath}.navEnhetId`}
						label={'NAV-kontor'}
						size={'xxlarge'}
						options={navEnheter}
					/>
				</div>
			</Panel>
		</Vis>
	)
}

UforetrygdForm.validation = validation
