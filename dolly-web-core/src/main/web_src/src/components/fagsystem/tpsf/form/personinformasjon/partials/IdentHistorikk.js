import React from 'react'
import { FieldArray } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'

const hjelpetekst =
	'Dette er en oversikt over utgåtte identer, der de eldste skal ligge sist. ' +
	'Dagens identtype legges inn på forrige side. For å velge dagens kjønn må "Kjønn" hukes av på forrige side og velges under "Diverse" -> "Kjønn" her. ' +
	'Eksempel: En testperson med DNR får FNR. Da velges FNR på forrige side. DNR legges inn i denne oversikten. ' +
	'Hvis fødselsdatoen ble endret i overgangen kan født før og født etter fylles ut. Det samme gjelder for kjønn. ' +
	'Dersom de står som "Ikke spesifisert" beholdes samme fødselsdato og/eller kjønn.'

export const IdentHistorikk = ({ formikBag }) => {
	const initialValues = formikBag.initialValues.tpsf.identHistorikk[0]
	return (
		<FieldArray
			name="tpsf.identHistorikk"
			render={arrayHelpers => (
				<Kategori title="Identhistorikk" hjelpetekst={hjelpetekst}>
					{formikBag.values.tpsf.identHistorikk.map((curr, idx) => (
						<div key={idx}>
							<FormikSelect
								name={`tpsf.identHistorikk.${idx}.identtype`}
								label="Identtype"
								options={Options('identtype')}
							/>
							<FormikSelect
								name={`tpsf.identHistorikk.${idx}.kjonn`}
								label="Kjønn"
								kodeverk="Kjønnstyper"
							/>
							<FormikDatepicker name={`tpsf.identHistorikk.${idx}.regdato`} label="Utgått dato" />
							<FormikDatepicker name={`tpsf.identHistorikk.${idx}.foedtEtter`} label="Født etter" />
							<FormikDatepicker name={`tpsf.identHistorikk.${idx}.foedtFoer`} label="Født før" />
							<FieldArrayRemoveButton onClick={e => arrayHelpers.remove(idx)} />
						</div>
					))}

					<FieldArrayAddButton
						title="Identhistorikk"
						onClick={e => arrayHelpers.push(initialValues)}
					/>
				</Kategori>
			)}
		/>
	)
}
