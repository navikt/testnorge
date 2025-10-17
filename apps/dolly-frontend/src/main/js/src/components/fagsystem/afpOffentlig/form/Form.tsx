import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { useFormContext } from 'react-hook-form'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialMocksvar } from '@/components/fagsystem/afpOffentlig/initialValues'
import { useMuligeDirektekall, useTpOrdningKodeverk } from '@/utils/hooks/usePensjon'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { BelopForm } from '@/components/fagsystem/afpOffentlig/form/BeloepForm'
import { validation } from '@/components/fagsystem/afpOffentlig/form/validation'
import { MocksvarTypes } from '@/components/fagsystem/afpOffentlig/afpOffentligTypes'

export const afpOffentligPath = 'pensjonforvalter.afpOffentlig'

export const AfpOffentligForm = () => {
	const formMethods = useFormContext()
	if (!formMethods.watch(afpOffentligPath)) {
		return null
	}

	const { tpOrdningData } = useTpOrdningKodeverk()
	const { direktekallData } = useMuligeDirektekall()

	const valgteDirektekall = formMethods.watch(`${afpOffentligPath}.direktekall`)
	const filterTpOrdningOptions = tpOrdningData?.filter(
		(option: any) => !valgteDirektekall?.includes(option.value),
	)

	const valgteTpOrdninger = formMethods
		.watch(`${afpOffentligPath}.mocksvar`)
		?.map((mocksvar: MocksvarTypes) => mocksvar.tpId)
	const filterDirektekallOptions = direktekallData?.filter(
		(option: any) => !valgteTpOrdninger?.includes(option.value),
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
						{(formPath: string, idx: number) => (
							<React.Fragment key={idx}>
								<div className={'flexbox--flex-wrap'}>
									<FormSelect
										name={`${formPath}.tpId`}
										label="TP-ordning"
										size={'xxlarge'}
										options={filterTpOrdningOptions}
									/>
									<FormSelect
										name={`${formPath}.statusAfp`}
										label="Status AFP"
										size={'medium'}
										options={Options('statusAfp')}
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

AfpOffentligForm.validation = validation
