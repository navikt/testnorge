import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import _has from 'lodash/has'
import _isEmpty from 'lodash/isEmpty'
import {
	DollyFieldArrayWrapper,
	DollyFaBlokk,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import { nesteGyldigStatuser, tomSisteSivilstand } from './SivilstandOptions'
import SivilstandForm from './sivilstandForm'

const initialValues = { sivilstand: '', sivilstandRegdato: '' }

export const Sivilstand = ({
	basePath,
	formikBag,
	locked,
	sivilstander,
	minDatoSivilstand,
	vurderFjernePartner
}) => (
	<FieldArray name={basePath}>
		{arrayHelpers => {
			const antallTidligereSivilstander = sivilstander.reduce(
				(acc, curr) => (!curr.ny ? acc + 1 : acc),
				0
			)

			// Sjekk forrige (nest siste) sivilstandstatus, for å sette
			// gyldige options for current sivilstandstatus
			let sivilstandKode
			if (sivilstander.length > 1) {
				sivilstandKode = sivilstander[sivilstander.length - 2].data.sivilstand
			}
			const options = nesteGyldigStatuser(sivilstandKode)

			const ugyldigSisteSivilstand =
				_has(formikBag.errors, basePath) || tomSisteSivilstand(formikBag, basePath)

			const addNewEntry = () => arrayHelpers.push(initialValues)
			return (
				<DollyFieldArrayWrapper header="Forhold" nested>
					{sivilstander.map((c, idx) => {
						const formikIdx = idx - antallTidligereSivilstander
						const formikPath = `${basePath}[${formikIdx}]`
						const isLast = idx === sivilstander.length - 1

						// Det er kun mulig å slette siste forhold
						const showRemove = idx > 0 && isLast && !locked && c.ny
						const clickRemove = () => {
							arrayHelpers.remove(formikIdx)
							if (formikIdx === 0) vurderFjernePartner()
						}
						return (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								header="Forhold"
								handleRemove={showRemove && clickRemove}
							>
								<SivilstandForm
									formikPath={formikPath}
									sivilstand={c.data}
									formikBag={formikBag}
									options={options}
									tidligereSivilstand={!c.ny}
									readOnly={!isLast || !c.ny || locked}
									minimumDato={minDatoSivilstand}
								/>
							</DollyFaBlokk>
						)
					})}
					<FieldArrayAddButton
						addEntryButtonText="Nytt forhold"
						hoverText={
							ugyldigSisteSivilstand
								? 'Siste sivilstand må være gyldig før du kan legge til en ny'
								: locked
								? 'Du kan kun endre siste partner'
								: false
						}
						disabled={ugyldigSisteSivilstand || locked}
						onClick={addNewEntry}
					/>
				</DollyFieldArrayWrapper>
			)
		}}
	</FieldArray>
)
