import React from 'react'
import { useAsyncFn } from 'react-use'
import _get from 'lodash/get'
import { DollyApi } from '~/service/Api'
import DollyModal from '~/components/ui/modal/DollyModal'
import Loading from '~/components/ui/loading/Loading'
import JiraLenker from '~/components/bestilling/jiraLenker/JiraLenker'
import NavButton from '~/components/ui/button/NavButton/NavButton'

export default function SendOpenAm({ closeModal, bestilling, getBestillinger }) {
	const [requestState, postOpenAm] = useAsyncFn(async () => {
		return DollyApi.postOpenAmBestilling(bestilling.id)
	})

	const openAmLinkArray = _get(requestState, 'value.data', []).map(response => response.message)

	const close = () => (requestState.value ? getBestillinger() : closeModal())

	return (
		<DollyModal isOpen={true} closeModal={close} width="25%" overflow="auto">
			<h1>Send til OpenAm</h1>

			{!requestState.value && (
				<p>
					Dolly vil sende alle personer i bestillingen til OpenAm. Er du sikker på at du vil utføre
					dette?
				</p>
			)}

			{requestState.loading && <Loading label="sender til openAm" />}

			{requestState.value && (
				<div className="bestilling-detaljer">
					<JiraLenker openAm={openAmLinkArray} />
					<p>Sakene vil bli oppdatert i løpet av 2-3 minutter, og finnes da i kommentarfeltet.</p>

					<div className="dollymodal_buttons">
						<NavButton type={'fare'} autoFocus onClick={close}>
							Lukk
						</NavButton>
					</div>
				</div>
			)}

			{!requestState.value && (
				<div className="dollymodal_buttons">
					<NavButton type={'fare'} autoFocus onClick={closeModal}>
						Avbryt
					</NavButton>
					<NavButton type="hoved" onClick={postOpenAm}>
						Send til OpenAm
					</NavButton>
				</div>
			)}
		</DollyModal>
	)
}
