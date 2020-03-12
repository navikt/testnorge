import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import {
	DollyFieldArrayWrapper,
	DollyFaBlokk,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import { nesteGyldigStatuser, statuser as SivilstandStatuser } from './SivilstandOptions'

const initialValues = { sivilstand: '', sivilstandRegdato: null }

export const Sivilstand = ({ basePath, formikBag, locked, erSistePartner }) => (
	<FieldArray name={basePath}>
		{arrayHelpers => {
			const sivilstander = _get(arrayHelpers.form.values, basePath, [])

			// Sjekk forrige (nest siste) sivilstandstatus, for å sette
			// gyldige options for current sivilstandstatus
			let sivilstandKode
			if (sivilstander.length > 1) {
				sivilstandKode = sivilstander[sivilstander.length - 2].sivilstand
			}

			const options = nesteGyldigStatuser(sivilstandKode)
			const ingenSivilstand = _get(formikBag.values, basePath)[0].sivilstand.length < 1
			const ugyldigSisteSivilstand = _has(formikBag.errors, basePath) || ingenSivilstand

			const addNewEntry = () => arrayHelpers.push(initialValues)
			return (
				<DollyFieldArrayWrapper header="Forhold" nested>
					{sivilstander.map((c, idx) => {
						const path = `${basePath}[${idx}]`
						const isLast = idx === sivilstander.length - 1

						// Det er kun mulig å slette siste forhold
						const showRemove = isLast && idx > 0 && !locked
						const clickRemove = () => arrayHelpers.remove(idx)
						return (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								header="Forhold"
								handleRemove={showRemove && clickRemove}
							>
								<SivilstandForm path={path} options={options} readOnly={!isLast || locked} />
							</DollyFaBlokk>
						)
					})}
					<FieldArrayAddButton
						addEntryButtonText="Nytt forhold"
						hoverText={
							ugyldigSisteSivilstand
								? 'Siste sivilstand må være gyldig før du kan legge til en ny'
								: !erSistePartner
								? 'Du kan kun endre siste partner'
								: false
						}
						disabled={ugyldigSisteSivilstand || !erSistePartner}
						onClick={addNewEntry}
					/>
				</DollyFieldArrayWrapper>
			)
		}}
	</FieldArray>
)

const SivilstandForm = ({ path, options, readOnly }) => (
	<div className="flexbox" title={readOnly ? 'Du kan kun endre siste partner' : undefined}>
		<FormikSelect
			name={`${path}.sivilstand`}
			label="Forhold til partner (sivilstand)"
			options={readOnly ? Object.values(SivilstandStatuser) : options}
			isClearable={false}
			disabled={readOnly}
			fastfield={false}
		/>
		<FormikDatepicker
			name={`${path}.sivilstandRegdato`}
			label="Sivilstand fra dato"
			isClearable={false}
			disabled={readOnly}
			fastfield={false}
		/>
	</div>
)
