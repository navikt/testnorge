import React, { useState } from 'react'
import { actions } from '@/ducks/gruppe'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import Button from '@/components/ui/button/Button'
import Loading from '@/components/ui/loading/Loading'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { Alert } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useDispatch } from 'react-redux'
import { useTags } from '@/utils/hooks/useTags'
import './TagsButton.less'

type Props = {
	gruppeId: number
	eksisterendeTags: string[]
	isSending: boolean
}

export const TagsButton = ({ gruppeId, eksisterendeTags, isSending }: Props) => {
	const dispatch = useDispatch()
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [tags, setTags] = useState(eksisterendeTags)
	const { tagOptions, loading } = useTags()

	if (isSending) {
		return <Loading label="Sender tags..." />
	}
	if (loading) {
		return <Loading label="Laster tags..." />
	}

	const handleSubmit = () => {
		dispatch(actions.sendGruppeTags(gruppeId, tags))
		closeModal()
	}

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_TILKNYTT_TAGS}
				onClick={openModal}
				kind="link"
				className="svg-icon-blue"
			>
				TAGS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<div className="tagsModal">
					<div className="tagsModal tagsModal-content">
						<h1>Tilknytt tags</h1>
						<Alert variant="info">
							Tags gir deg mulighet til å identifisere dine PDL-personer på egen “tagged”
							Kafka-topic, der tags[dintag] legges til på responsen. Ta kontakt for ytterligere
							informasjon.
						</Alert>
						<h4>Velg hvilke tags du ønsker å legge til</h4>
						<DollySelect
							name={'tags'}
							options={tagOptions}
							isMulti
							value={tags}
							size={'large'}
							onChange={(selected: any) => {
								setTags(selected.map((s: any) => s.value))
							}}
						/>
					</div>
					<div className="tagsModal-actions">
						<NavButton variant="danger" onClick={closeModal}>
							Avbryt
						</NavButton>
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_POST_TAGS}
							onClick={handleSubmit}
							variant="primary"
						>
							Tilknytt tags
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</>
	)
}
