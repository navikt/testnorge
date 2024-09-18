import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { useFormContext } from 'react-hook-form'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialAfpOffentlig } from '@/components/fagsystem/afpOffentlig/initialValues'
import { useTpOrdning } from '@/utils/hooks/usePensjon'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { BelopForm } from '@/components/fagsystem/afpOffentlig/form/BeloepForm'

export const afpOffentligPath = 'pensjonforvalter.afpOffentlig'

export const AfpOffentligForm = () => {
	const formMethods = useFormContext()

	const { tpOrdningData, loading, error } = useTpOrdning()

	return (
		<Vis attributt={afpOffentligPath}>
			<Panel
				heading="AFP offentlig"
				hasErrors={panelError(afpOffentligPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [afpOffentligPath])}
			>
				<FormDollyFieldArray
					name={`${afpOffentligPath}.mocksvar`}
					header="AFP offentlig"
					newEntry={initialAfpOffentlig}
					canBeEmpty={false}
				>
					{(formPath, idx) => (
						<React.Fragment key={idx}>
							<div className={'flexbox--flex-wrap'}>
								<FormSelect
									name={`${formPath}.tpId`}
									label="TP-ordning"
									size={'xxlarge'}
									options={tpOrdningData}
									// isClearable={false}
								/>
								<FormSelect
									name={`${formPath}.statusAfp`}
									label="Status AFP"
									size={'medium'}
									options={Options('statusAfp')}
									// isClearable={false}
								/>
								<FormDatepicker name={`${formPath}.virkningsDato`} label="Virkningsdato" />
								<FormSelect
									name={`${formPath}.sistBenyttetG`}
									label="Sist benyttet G"
									size={'medium'}
									options={getYearRangeOptions(1968, new Date().getFullYear())}
								/>
							</div>
							<BelopForm path={`${formPath}.belopsListe`} />
						</React.Fragment>
					)}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}
