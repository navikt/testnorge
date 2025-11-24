import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { vi } from 'vitest';
import { TpsMeldingerPage } from './TpsMeldingerPage';
import { useTpsMessagingXml } from '../../hooks/useTpsMessaging';
import { sendTpsMelding } from '../../service/SendTpsMeldingService';
import { dollyTest } from '../../../vitest.setup';

vi.mock('../../service/SendTpsMeldingService', () => ({
  sendTpsMelding: vi.fn(),
}));

vi.mock('../../hooks/useTpsMessaging', () => ({
  useTpsMessagingXml: vi.fn(),
}));

describe('TpsMeldingerPage', () => {
  const user = userEvent.setup();

  beforeEach(() => {
    vi.mocked(sendTpsMelding).mockResolvedValue('Opprettet melding');

    vi.mocked(useTpsMessagingXml).mockReturnValue({
      queues: [
        { value: 'queue1', label: 'queue1' },
        { value: 'queue2', label: 'queue2' },
        { value: 'queue2', label: 'queue2' },
      ],
      loading: false,
      error: undefined,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  dollyTest('should show validation errors when submitting empty form', async () => {
    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    // Submit without filling form
    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    // Verify error messages appear
    await waitFor(() => {
      expect(screen.getByText('Melding kan ikke være tom')).toBeInTheDocument();
      expect(screen.getByText('Du må velge en kø.')).toBeInTheDocument();
    });

    expect(sendTpsMelding).not.toHaveBeenCalled();
  });

  dollyTest('should show error when message is missing', async () => {
    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    await user.click(screen.getByLabelText('Meldingskø'));
    await user.click(screen.getByText('queue1'));
    await user.clear(screen.getByLabelText('TPS melding'));
    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    await waitFor(() => {
      expect(screen.getByText('Melding kan ikke være tom')).toBeInTheDocument();
    });

    expect(sendTpsMelding).not.toHaveBeenCalled();
  });

  dollyTest('should show error when queue is missing', async () => {
    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    // Enter only message
    await user.type(screen.getByLabelText('TPS melding'), '<xml>Test</xml>');
    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    await waitFor(() => {
      expect(screen.getByText('Du må velge en kø.')).toBeInTheDocument();
    });

    expect(sendTpsMelding).not.toHaveBeenCalled();
  });

  dollyTest('should render loading state', () => {
    vi.mocked(useTpsMessagingXml).mockReturnValue({
      queues: [],
      loading: true,
      error: undefined,
    });

    render(<TpsMeldingerPage />);
    expect(screen.getByText('Henter køer...')).toBeInTheDocument();
  });

  dollyTest('should render error state', () => {
    vi.mocked(useTpsMessagingXml).mockReturnValue({
      queues: [],
      loading: false,
      error: new Error('Test error'),
    });

    render(<TpsMeldingerPage />);

    // Check for the error alert by class instead of role
    const errorAlert = document.querySelector('.navds-alert--error');
    expect(errorAlert).toBeInTheDocument();

    expect(screen.getByText(/Noe gikk galt/i)).toBeInTheDocument();

    const errorIcon = document.querySelector('svg.navds-alert__icon[role="img"]');
    expect(errorIcon).toBeInTheDocument();
  });

  dollyTest('should send form after valid input', async () => {
    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    await user.click(screen.getByLabelText('Meldingskø'));
    await user.click(screen.getByText('queue1'));
    await user.clear(screen.getByLabelText('TPS melding'));
    await user.type(screen.getByLabelText('TPS melding'), '<xml>Test message</xml>');

    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    // Check that sendTpsMelding was called and success message shown
    await waitFor(() => {
      expect(sendTpsMelding).toHaveBeenCalledWith('queue1', '<xml>Test message</xml>');
      expect(screen.getByText('Opprettet melding')).toBeInTheDocument();
    });
  });
  dollyTest('should validate XML when queue contains "xml"', async () => {
    vi.mocked(useTpsMessagingXml).mockReturnValue({
      queues: [
        { value: 'xml-queue', label: 'xml-queue' },
        { value: 'info-queue', label: 'info-queue' },
      ],
      loading: false,
      error: undefined,
    });

    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    // Select XML queue
    await user.click(screen.getByLabelText('Meldingskø'));
    await user.click(screen.getByText('xml-queue'));

    // Enter invalid XML
    await user.type(screen.getByLabelText('TPS melding'), '<xml>Invalid</missing-tag>');

    // Submit form
    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    await waitFor(() => {
      expect(screen.getByText(/Ugyldig XML-format/)).toBeInTheDocument();
    });

    expect(sendTpsMelding).not.toHaveBeenCalled();
  });

  dollyTest('should not validate XML when queue does not contain "xml"', async () => {
    vi.mocked(useTpsMessagingXml).mockReturnValue({
      queues: [
        { value: 'xml-queue', label: 'xml-queue' },
        { value: 'info-queue', label: 'info-queue' },
      ],
      loading: false,
      error: undefined,
    });

    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    // Select non-XML queue
    await user.click(screen.getByLabelText('Meldingskø'));
    await user.click(screen.getByText('info-queue'));

    // Enter invalid XML
    await user.clear(screen.getByLabelText('TPS melding'));
    await user.type(screen.getByLabelText('TPS melding'), 'Tilfeldig info-tekst');

    // Submit form
    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    // Check that the form was submitted despite invalid XML
    await waitFor(() => {
      expect(sendTpsMelding).toHaveBeenCalledWith('info-queue', 'Tilfeldig info-tekst');
    });
  });

  dollyTest('should render PrettyCode component after successful submission', async () => {
    const xmlResponse =
      '<?xml version="1.0" encoding="UTF-8"?>\\n\' +\n' +
      "        '<tpsPersonData>\\n' +\n" +
      "        '<tpsServiceRutine>\\n' +\n" +
      "        '<serviceRutinenavn>FS03-FDNUMMER-PERSDATA-O</serviceRutinenavn>\\n' +\n" +
      "        '<fnr>DDMMÅÅNNNNN</fnr>\\n' +\n" +
      "        '<aksjonsDato> </aksjonsDato>\\n' +\n" +
      "        '<aksjonsKode>A</aksjonsKode>\\n' +\n" +
      "        '<aksjonsKode2>0</aksjonsKode2>\\n' +\n" +
      "        '</tpsServiceRutine>\\n' +\n" +
      "        '</tpsPersonData>'";
    vi.mocked(sendTpsMelding).mockResolvedValue(xmlResponse);

    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    await user.click(screen.getByLabelText('Meldingskø'));
    await user.click(screen.getByText('queue1'));
    await user.type(screen.getByLabelText('TPS melding'), '<xml>Test message</xml>');

    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    await waitFor(() => {
      const prettyCodeElement = screen.getByTestId('pretty-code-component');
      expect(prettyCodeElement).toBeInTheDocument();
    });

    const successIcon = document.querySelector('svg.navds-alert__icon[role="img"]');
    expect(successIcon).toBeInTheDocument();
  });

  dollyTest('should allow adding and submitting a custom queue value', async () => {
    render(<TpsMeldingerPage />);

    await waitFor(() => {
      expect(screen.queryByText('Henter køer...')).not.toBeInTheDocument();
    });

    const combobox = screen.getByLabelText('Meldingskø');
    await user.click(combobox);

    // Type a custom value
    const customQueueName = 'custom-queue-name';
    await user.clear(combobox);
    await user.type(combobox, customQueueName);

    // Click the "Legg til" option that should appear
    const addOptionElement = screen.getByRole('option', {
      name: (content, element) => element.textContent?.includes('Legg til') || false,
    });
    await user.click(addOptionElement);

    await user.clear(screen.getByLabelText('TPS melding'));
    await user.type(screen.getByLabelText('TPS melding'), 'Test message content');

    await user.click(screen.getByRole('button', { name: 'Send inn' }));

    // Verify the form was submitted with the custom queue value
    await waitFor(() => {
      expect(sendTpsMelding).toHaveBeenCalledWith(customQueueName, 'Test message content');
    });
  });
});
