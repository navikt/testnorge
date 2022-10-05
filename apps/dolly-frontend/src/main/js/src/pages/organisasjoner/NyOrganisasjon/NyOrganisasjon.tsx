import React, { useState } from 'react'
import { Formik, FormikProps } from 'formik'
import { useToggle } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '~/components/ui/button/Button'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'

import styled from 'styled-components'
import { useDollyOrganisasjonMaler } from '~/utils/hooks/useMaler'
import {
	getBrukerOptions,
	getMalOptions,
	NyBestillingProps,
} from '~/components/bestillingsveileder/startModal/NyIdent/NyIdent'

const initialValues = {
	mal: null as string,
}

const InputDiv = styled.div`
	margin-top: 10px;
`

export const NyOrganisasjon = ({ onAvbryt, onSubmit, brukernavn }: NyBestillingProps) => {
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const { maler, loading } = useDollyOrganisasjonMaler()

	const brukerOptions = getBrukerOptions(maler)
	const malOptions = getMalOptions(maler, bruker)

	const handleMalChange = (formikbag: FormikProps<any>) => {
		toggleMalAktiv()
		if (formikbag.values.mal) {
			formikbag.setFieldValue('mal', null)
		}
	}

	const handleBrukerChange = (event: { value: any }, formikbag: FormikProps<any>) => {
		setBruker(event.value)
		formikbag.setFieldValue('mal', null)
	}

	const preSubmit = (values: { mal: any }, formikBag: any) => {
		if (values.mal) values.mal = malOptions.find((m) => m.value === values.mal)?.data
		return onSubmit(values, formikBag)
	}

	return (
		<Formik initialValues={initialValues} onSubmit={preSubmit}>
			{(formikBag) => {
				return (
					<div className="ny-bestilling-form">
						<div className="ny-bestilling-form_maler">
							<div>
								<DollyCheckbox
									name="aktiver-maler"
									onChange={() => handleMalChange(formikBag)}
									label="Opprett fra mal"
									wrapperSize={'none'}
									size={'small'}
									isSwitch
								/>
							</div>

							<InputDiv>
								<DollySelect
									name="zIdent"
									label="Bruker"
									isLoading={loading}
									options={brukerOptions}
									size="medium"
									onChange={(e: { value: any }) => handleBrukerChange(e, formikBag)}
									value={bruker}
									isClearable={false}
									disabled={!malAktiv}
								/>
								<FormikSelect
									name="mal"
									label="Maler"
									isLoading={loading}
									options={malOptions}
									size="grow"
									fastfield={false}
									disabled={!malAktiv}
								/>
							</InputDiv>
							<div className="mal-admin">
								<Button kind="maler">
									<NavLink to="/minside">Administrer maler</NavLink>
								</Button>
							</div>
						</div>
						<ModalActionKnapper
							submitknapp="Start bestilling"
							disabled={!formikBag.isValid || formikBag.isSubmitting}
							onSubmit={formikBag.handleSubmit}
							onAvbryt={onAvbryt}
							center
						/>
					</div>
				)
			}}
		</Formik>
	)
}
