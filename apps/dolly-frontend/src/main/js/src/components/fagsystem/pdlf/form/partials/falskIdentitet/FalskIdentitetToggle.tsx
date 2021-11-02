import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import _get from 'lodash/get'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikProps } from 'formik'

interface FalskIdentitetValues {
	erFalsk: boolean
	rettIdentitetVedIdentifikasjonsnummer?: string
}

interface FalskIdentitetToggleProps {
	formikBag: FormikProps<{
		pdldata: {
			person: {
				falskIdentitet: Array<FalskIdentitetValues>
			}
		}
	}>
	path: string
}

export const FalskIdentitetToggle = ({ formikBag, path }: FalskIdentitetToggleProps) => {
	const [velgEksisterende, setVelgEksisterende, setOpprettNy] = useBoolean(
		!_get(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`) ? false : true
	)

	const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	return (
		<div
			className="flexbox--align-center"
			title="For øyeblikket er det ikke mulig å velge eksisterende ident - ved bestilling vil det automatisk opprettes en ny ident."
		>
			<FormikCheckbox
				name={`${path}.eksisterende`}
				label="Velg eksisterende ident"
				onChange={velgEksisterende ? setOpprettNy : setVelgEksisterende}
				checkboxMargin
				disabled={true}
			/>
			{velgEksisterende && (
				// TODO: Tilpass denne når vi kan hente eksisterende identer fra pdl
				<DollySelect
					name={`${path}.rettIdentitetVedIdentifikasjonsnummer`}
					label="Navn og identifikasjonsnummer"
					size="large"
					options={navnOgFnrOptions}
					isLoading={dollyGruppeInfo.loading}
					onChange={(id) =>
						formikBag.setFieldValue(
							`${path}.rettIdentitetVedIdentifikasjonsnummer`,
							id ? id.value : null
						)
					}
					value={_get(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)}
					disabled={true}
				/>
			)}
		</div>
	)
}
