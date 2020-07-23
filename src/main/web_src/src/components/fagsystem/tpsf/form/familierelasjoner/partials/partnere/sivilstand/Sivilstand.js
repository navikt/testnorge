import React from 'react'
import { FieldArray } from 'formik'
import _has from 'lodash/has'
import {
	DollyFieldArrayWrapper,
	DollyFaBlokk,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import { nesteGyldigStatuser, tomSisteSivilstand } from './SivilstandOptions'
import SivilstandForm from './sivilstandForm'

const isSivilstandNy = sivilstand => sivilstand.ny || !sivilstand.hasOwnProperty('ny')
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
			const antallTidligereSivilstander = sivilstander.filter(
				sivilstand => !isSivilstandNy(sivilstand)
			).length

			// Sjekk forrige (nest siste) sivilstandstatus, for å sette
			// gyldige options for current sivilstandstatus
			const options = nesteGyldigStatuser(sivilstander)

			const ugyldigSisteSivilstand =
				_has(formikBag.errors, basePath) || tomSisteSivilstand(formikBag, basePath)

			const addNewEntry = () => arrayHelpers.push(initialValues)
			return (
				<DollyFieldArrayWrapper header="Forhold" nested>
					{sivilstander.map((sivilstand, idx) => {
						const formikIdx = idx - antallTidligereSivilstander
						const formikPath = `${basePath}[${formikIdx}]`
						const isLast = idx === sivilstander.length - 1
						const ny = isSivilstandNy(sivilstand)

						// Det er kun mulig å slette siste forhold
						const showRemove = idx > 0 && isLast && !locked && ny
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
									sivilstand={sivilstand}
									formikBag={formikBag}
									options={options}
									tidligereSivilstand={!ny}
									readOnly={!isLast || !ny || locked}
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
