import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { useFormContext } from 'react-hook-form'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialMocksvar } from '@/components/fagsystem/afpOffentlig/initialValues'
import { useMuligeDirektekall, useTpOrdning } from '@/utils/hooks/usePensjon'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { BelopForm } from '@/components/fagsystem/afpOffentlig/form/BeloepForm'

export const afpOffentligPath = 'pensjonforvalter.afpOffentlig'

export const AfpOffentligForm = () => {
	const formMethods = useFormContext()

	const { tpOrdningData } = useTpOrdning()
	const { direktekallData } = useMuligeDirektekall()

	const valgteDirektekall = formMethods.watch(`${afpOffentligPath}.direktekall`)
	const filterTpOrdningOptions = tpOrdningData?.filter(
		(option) => !valgteDirektekall?.includes(option.value),
	)

	const valgteTpOrdninger = formMethods
		.watch(`${afpOffentligPath}.mocksvar`)
		?.map((mocksvar) => mocksvar.tpId)
	const filterDirektekallOptions = direktekallData?.filter(
		(option) => !valgteTpOrdninger?.includes(option.value),
	)

	return (
		<Vis attributt={afpOffentligPath}>
			<Panel
				heading="AFP offentlig"
				hasErrors={panelError(afpOffentligPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [afpOffentligPath])}
			>
				<>
					<div title={filterDirektekallOptions?.length === 0 ? 'Ingen tilgjengelige valg' : ''}>
						<FormSelect
							name={`${afpOffentligPath}.direktekall`}
							label="Direktekall"
							size={'grow'}
							options={filterDirektekallOptions}
							isDisabled={filterDirektekallOptions?.length === 0}
							isMulti
						/>
					</div>
					<FormDollyFieldArray
						name={`${afpOffentligPath}.mocksvar`}
						header="AFP offentlig"
						newEntry={initialMocksvar}
						canBeEmpty={false}
					>
						{(formPath, idx) => (
							<React.Fragment key={idx}>
								<div className={'flexbox--flex-wrap'}>
									<FormSelect
										name={`${formPath}.tpId`}
										label="TP-ordning"
										size={'xxlarge'}
										options={filterTpOrdningOptions}
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
				</>
			</Panel>
		</Vis>
	)
}
