import React, { useState } from 'react'
import * as yup from 'yup'
import useBoolean from '~/utils/hooks/useBoolean'
import { useAsync } from 'react-use'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { DollyApi } from '~/service/Api'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Formik, Form } from 'formik'

export default function ImporterGrupper({ importZIdent, fetchMineGrupper }) {
	const [isImportModalOpen, openImportModal, closeImportModal] = useBoolean(false)

	const ZIdenter = useAsync(async () => {
		const response = await DollyApi.getBrukere()
		return response.data
	}, [])

	const getZIdentOptions = () => {
		return ZIdenter.value.reduce(function(filtered, ident) {
			if (ident.navIdent) {
				filtered.push({ value: ident.navIdent, label: ident.navIdent })
			}
			return filtered
		}, [])
	}

	const ZIdentOptions = ZIdenter.value ? getZIdentOptions() : []

	const importerZIdenter = ({ identer }) => {
		let request = identer[0]
		if (identer.length > 1) {
			for (let i = 1; i < identer.length; i++) {
				request = request.concat(`&navIdenter=${identer[i]}`)
			}
		}
		importZIdent(request)
		closeImportModal()
		// fetchMineGrupper()
	}

	const validation = yup.object().shape({
		identer: yup.array().required('Velg minst én Z-ident')
	})

	return (
		<>
			<p>Du har for øyeblikket ingen testdatagrupper på denne brukerkontoen.</p>
			<p>
				Om dette er første gang du bruker din personlige brukerkonto kan du importere
				testdatagrupper Z-brukeren(e) du har benyttet tidligere ved å trykke på knappen nedenfor. Du
				kan når som helst importere testdatagrupper fra Z-brukere via Min side øverst til høyre.
			</p>
			<p>For å opprette en ny testdatagruppe, trykk på "Ny gruppe"-knappen over.</p>
			<NavButton type="hoved" onClick={openImportModal} style={{ marginTop: '10px' }}>
				Importer grupper
			</NavButton>

			<DollyModal isOpen={isImportModalOpen} closeModal={closeImportModal}>
				<>
					<h1>Importer testdatagrupper fra Z-ident</h1>
					<p>Velg hvilke Z-identer du ønsker å importere.</p>
					<p>OBS - vær klar over følgende:</p>
					<ul>
						<li>
							Du kan importere så mange Z-brukere du ønsker. Har du flere Z-brukere vil altså alle
							dataene fra disse kunne samles på din personlige brukerkonto.
						</li>
						<li>
							Hver Z-bruker kan kun importeres av én personlig brukerkonto. Her er det førstemann
							til mølla-prinsippet som gjelder, dvs. at når du importerer en Z-bruker vil ingen
							andre kunne importere den samme.
						</li>
					</ul>

					<Formik initialValues={{}} onSubmit={importerZIdenter} validationSchema={validation}>
						{() => (
							<Form>
								<FormikSelect
									name="identer"
									label="Z-identer"
									options={ZIdentOptions}
									isLoading={ZIdenter.loading}
									isMulti={true}
									size="grow"
									isClearable={false}
									fastfield={false}
								/>
								<NavButton type="standard" onClick={closeImportModal}>
									Avbryt
								</NavButton>
								<NavButton type="hoved" htmlType="submit">
									Importer grupper
								</NavButton>
							</Form>
						)}
					</Formik>
				</>
			</DollyModal>
		</>
	)
}
