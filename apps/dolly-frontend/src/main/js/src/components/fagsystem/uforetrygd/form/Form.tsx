import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React, { useEffect, useState } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { genererTilfeldigeNavPersonidenter } from '@/utils/GenererTilfeldigeNavPersonidenter'
import * as _ from 'lodash-es'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

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
					<FormikDatepicker
						name={`${uforetrygdPath}.kravFremsattDato`}
						label="Krav fremsatt dato"
					/>
					<FormikDatepicker
						name={`${uforetrygdPath}.onsketVirkningsDato`}
						label="Ønsket virkningsdato"
					/>
					<FormikDatepicker name={`${uforetrygdPath}.uforetidspunkt`} label="Uføretidspunkt" />
					<FormikTextInput
						name={`${uforetrygdPath}.inntektForUforhet`}
						label="Inntekt før uførhet"
						type="number"
						fastfield="false"
					/>
					<FormikCheckbox
						name={`${uforetrygdPath}.barnetilleggDetaljer`}
						label="Har barnetillegg"
						size="small"
					/>
				</div>
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${uforetrygdPath}.minimumInntektForUforhetType`}
						label="Sats for minimum IFU"
						size="xlarge"
						options={Options('minimumInntektForUforhetType')}
					/>
					{/*	TODO: må ha annet navn*/}
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
						label={'NAV-enhet'}
						size={'xxxlarge'}
						options={navEnheter}
					/>
				</div>
			</Panel>
		</Vis>
	)
}
